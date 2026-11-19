package top.ezadmin.scaffold;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:scaffold-test;MODE=MySQL;DB_CLOSE_DELAY=-1"
})
class AssetWorkflowTest {

    private static final String LIST = "/p/list/";
    private static final String FORM = "/p/form/";
    private static final String ASSET = "demo/asset";

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate database;

    @Autowired
    private ObjectMapper json;

    private final HttpClient client = HttpClient.newHttpClient();

    @Test
    void servesConfiguredNavigationWithAndWithoutTheSystemRoot() throws Exception {
        HttpResponse<String> response = client.send(request("/laynavs.html").GET().build(), HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type").orElseThrow()).startsWith("application/json");
        JsonNode wrapped = json.readTree(response.body());
        assertThat(wrapped.path("code").asText()).isEqualTo("0");
        assertThat(wrapped.path("success").asBoolean()).isTrue();
        assertThat(wrapped.at("/data/homeInfo/href").asText()).isEqualTo(LIST + "page-" + ASSET);
        assertThat(wrapped.at("/data/logoInfo/title").asText()).isEqualTo("EzAdmin");
        assertThat(wrapped.at("/data/menuInfo/0/title").asText()).isEqualTo("系统");

        JsonNode menu = json.readTree(get("/laynavs.html?noroot=true")).at("/data/menuInfo");
        assertThat(menu).isEqualTo(wrapped.at("/data/menuInfo/0/child"));
        assertThat(menu.at("/0/child/0/title").asText()).isEqualTo("资产管理");
        String href = menu.at("/0/child/0/href").asText();
        assertThat(href).isEqualTo(LIST + "page-" + ASSET);
        assertThat(get(href)).contains("ASSET_NAME");
    }

    @Test
    void rendersAndSearchesTheSeededAssets() throws Exception {
        HttpResponse<String> home = client.send(request("/").GET().build(), HttpResponse.BodyHandlers.ofString());
        assertThat(home.statusCode()).isEqualTo(302);
        assertThat(URI.create(home.headers().firstValue("Location").orElseThrow()).getPath())
                .isEqualTo(LIST + "page-" + ASSET);
        assertThat(get(LIST + "page-" + ASSET)).contains("ASSET_NAME", "form/page-" + ASSET);
        assertThat(get(FORM + "page-" + ASSET)).contains("submitbtn", "ASSET_NAME");

        JsonNode assets = json.readTree(get(LIST + "data-" + ASSET + "?page=1&perPageInt=10&ASSET_NAME=Projector"));
        assertThat(assets.path("success").asBoolean()).isTrue();
        assertThat(assets.path("data").size()).isEqualTo(1);
        assertThat(assets.at("/data/0/ASSET_NAME").asText()).isEqualTo("Projector");
    }

    @Test
    void createsUpdatesAndDeletesAnAssetThroughTheDsl() throws Exception {
        JsonNode created = post(FORM + "submit-" + ASSET, "ASSET_NAME=Camera&NOTES=Demo");
        long id = created.at("/data/ID").asLong();
        assertThat(id).isPositive();
        assertThat(json.readTree(get(FORM + "data-" + ASSET + "?ID=" + id)).at("/data/ASSET_NAME").asText())
                .isEqualTo("Camera");

        JsonNode updated = post(FORM + "submit-" + ASSET, "ID=" + id + "&ASSET_NAME=Camera+Kit&NOTES=Updated");
        assertThat(updated.at("/data/ID").asLong()).isEqualTo(id);
        assertThat(database.queryForObject("SELECT ASSET_NAME FROM DEMO_ASSET WHERE ID = ?", String.class, id))
                .isEqualTo("Camera Kit");

        JsonNode deleted = json.readTree(get(FORM + "delete-" + ASSET + "?ID=" + id));
        assertThat(deleted.path("success").asBoolean()).as(deleted.toString()).isTrue();
        assertThat(database.queryForObject("SELECT DELETED FROM DEMO_ASSET WHERE ID = ?", Boolean.class, id)).isTrue();
        JsonNode search = json.readTree(get(LIST + "data-" + ASSET + "?page=1&perPageInt=10&ASSET_NAME=Camera"));
        assertThat(search.path("success").asBoolean()).isTrue();
        assertThat(search.path("data").size()).isZero();
    }

    private HttpRequest.Builder request(String path) {
        return HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path));
    }

    private String get(String path) throws Exception {
        return send(request(path).GET());
    }

    private JsonNode post(String path, String params) throws Exception {
        String body = send(request(path)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(params)));
        JsonNode result = json.readTree(body);
        assertThat(result.path("success").asBoolean()).as(body).isTrue();
        return result;
    }

    private String send(HttpRequest.Builder request) throws Exception {
        HttpResponse<String> response = client.send(request.build(), HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).as(response.body()).isEqualTo(200);
        return response.body();
    }
}

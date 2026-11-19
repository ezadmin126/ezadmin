package top.ezadmin;

import org.junit.Assert;
import org.junit.Test;
import top.ezadmin.common.utils.EzJsonImpl;
import top.ezadmin.common.utils.JSONUtils;

public class EzJsonImplTest {

    @Test
    public void toJSONStringWithNull_shouldIncludeNullFields() {
        SampleDto sample = new SampleDto();
        sample.setName("test");

        String defaultJson = new EzJsonImpl().toJSONString(sample);
        String withNullJson = new EzJsonImpl().toJSONStringWithNull(sample);

        Assert.assertFalse(defaultJson.contains("\"remark\""));
        Assert.assertTrue(withNullJson.contains("\"name\":\"test\""));
        Assert.assertTrue(withNullJson.contains("\"remark\":null"));
    }

    @Test
    public void jsonUtilsToJSONStringWithNull_shouldDelegateToConfiguredEzJson() {
        SampleDto sample = new SampleDto();
        sample.setName("test");

        String json = JSONUtils.toJSONStringWithNull(sample);

        Assert.assertTrue(json.contains("\"name\":\"test\""));
        Assert.assertTrue(json.contains("\"remark\":null"));
    }

    public static class SampleDto {
        private String name;
        private String remark;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}

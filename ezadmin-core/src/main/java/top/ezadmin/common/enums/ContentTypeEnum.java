package top.ezadmin.common.enums;

import top.ezadmin.common.utils.StringUtils;

public enum ContentTypeEnum {

	HTML("html", "text/html;charset=UTF-8"),
	PLAIN_TEXT("plain", "text/plain;charset=UTF-8"),
	JSON("json", "application/json;charset=UTF-8"),
	XML("xml", "application/xml;charset=UTF-8"),
	PNG("png", "image/png"),
	JPEG("jpeg", "image/jpeg"),
	PDF("pdf", "application/pdf"),
	ZIP("zip", "application/zip"),
	MP3("mp3", "audio/mpeg"),
	MP4("mp4", "video/mp4"),
	JPG("jpg", "image/jpeg;charset=UTF-8"), //
	CSS("css", "text/css;charset=UTF-8"), //
	JS("js", "application/x-javascript;charset=UTF-8"), //
	GIF("gif", "image/gif;charset=UTF-8"), //
	TEXT ("text", "text/plain"), //
	STREAM ("stream", "application/octet-stream"), //
	WOFF2("woff2", "font/woff2"), //
	HTM("htm", "text/html;charset=UTF-8");
	String key;
	public String value;

	private ContentTypeEnum(String key, String value) {
		this.key = key;
		this.value = value;
	}


	public static String loadContentTypeByUrl(String url) {
		int lastIndex=0;
		if(StringUtils.isBlank(url)){
			return STREAM.value;
		}
		for(int i=0;i<url.length();i++){
			if(url.charAt(i)=='?'||url.charAt(i)==';'){
				break;
			}
			lastIndex=i;
		}
		StringBuilder stringBuilder=new StringBuilder();
		for(int i=lastIndex;i>0;i--){
			if(url.charAt(i)=='.'||url.charAt(i)=='/'){
				break;
			}
			stringBuilder.append(Character.toLowerCase(url.charAt(i)));
		}
		String suffix=stringBuilder.reverse().toString();

		for (ContentTypeEnum e : values()) {
			if (e.key.equals(suffix)) {
				return e.value;
			}
		}
		return STREAM.value;
	}
	public static String loadContentTypeByDownloadUrl(String url) {
		for (ContentTypeEnum e : values()) {
			if (url.toLowerCase().endsWith(e.key)) {
				return e.value;
			}
		}
		return STREAM.value;
	}


	public static void main(String[] args) {
		System.out.println(loadContentTypeByDownloadUrl("asdf?fileid=aaaa_jpg"));
	}



}

package myPoiSpider;

import java.util.ArrayList;
import java.util.List;

public class POIStruct {

	String name = "";
	String address = "";
	String url = "";
	List<String> poi = new  ArrayList<String>();
//	String phone = "";
//	String subLocality = "";
	String clearName = "";

//	public POIStruct(String name, String address, String url, List<String> poi, String phone, String subLocality,
//			String clearName) {
//		super();
//		this.name = name;
//		this.address = address;
//		this.url = url;
//		this.poi = poi;
//		this.phone = phone;
//		this.subLocality = subLocality;
//		this.clearName = clearName;
//	}

//	public POIStruct(String name, String address, String url, List<String> poi, String phone, String subLocality) {
//		super();
//		this.name = name;
//		this.address = address;
//		this.url = url;
//		this.poi = poi;
//		this.phone = phone;
//		this.subLocality = subLocality;
//	}

//	public POIStruct(String name, String address, String url) {
//		super();
//		this.name = name;
//		this.address = address;
//		this.url = url;
//	}

	public POIStruct(String name, String address, String url, List<String> poi) {
		super();
		this.name = name;
		this.address = address;
		this.url = url;
		this.poi = poi;
	}

//	public String toString() {
//		return "POIStruct [name=" + name + ", address=" + address + ", url=" + url + ", poi=" + poi + ", phone=" + phone
//				+ ", subLocality=" + subLocality + "]";
//	}

	
	
}

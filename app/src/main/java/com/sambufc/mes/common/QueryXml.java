package com.sambufc.mes.common;

import com.sambufc.mes.Application;
import com.sambufc.mes.model.Row;

import java.util.ArrayList;
import java.util.List;

public class QueryXml {

	public static String Login(String user, String pwd)
	{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<Root xmlns=\"http://www.nexacroplatform.com/platform/dataset\">"
				+ "<Parameters>"
				+ "<Parameter id=\"V_USER_ID\">" + user + "</Parameter>"
				+ "<Parameter id=\"V_KIOSK\">" + pwd + "</Parameter>"
				+ "<Parameter id=\"REG_PGM_ID\" />"
				+ "<Parameter id=\"DEF_LANG\" />"
				+ "<Parameter id=\"COMPANY_CD\">001</Parameter>"
				+ "</Parameters>"
				+ "</Root>";
		return xml;
	}

	public static String BoxInfo(String fromboxno, String toboxno){
		String xml =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
						"<Root xmlns=\"http://www.nexacroplatform.com/platform/dataset\">" +
						"<Parameters>" +
						"<Parameter id=\"method\">getList</Parameter>\n" +
						"<Parameter id=\"sqlId\">PPE020QR_BOX_NO_RANGE_S</Parameter>\n" +
						"<Parameter id=\"FROM_BOX_NO\">" + (fromboxno.compareTo(toboxno) < 0 ? fromboxno : toboxno) + "</Parameter>\n" +
						"<Parameter id=\"TO_BOX_NO\">" + (fromboxno.compareTo(toboxno) > 0 ? fromboxno : toboxno) + "</Parameter>\n" +
						"</Parameters>\n" +
						"<Dataset id=\"__DS_TRANS_INFO__\">\n" +
						"<ColumnInfo>\n" +
						"<Column id=\"strSvcID\" type=\"string\" size=\"256\"  />\n" +
						"<Column id=\"strURL\" type=\"string\" size=\"256\"  />\n" +
						"<Column id=\"strInDatasets\" type=\"string\" size=\"256\"  />\n" +
						"<Column id=\"strOutDatasets\" type=\"string\" size=\"256\"  />\n" +
						"</ColumnInfo>\n" +
						"<Rows>\n" +
						"<Row>\n" +
						"<Col id=\"strSvcID\">search</Col>\n" +
						"<Col id=\"strURL\">/nexacroController.do</Col>\n" +
						"<Col id=\"strInDatasets\" />\n" +
						"<Col id=\"strOutDatasets\">output1</Col>\n" +
						"</Row>\n" +
						"</Rows>\n" +
						"</Dataset>\n" +
						"</Root>";
		return xml;
	}

	public static String PalletInfo(String palletno){
		String xml =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
						"<Root xmlns=\"http://www.nexacroplatform.com/platform/dataset\">" +
						"<Parameters>" +
						"<Parameter id=\"method\">getList</Parameter>\n" +
						"<Parameter id=\"sqlId\">PPE020QR_PALLET_NO_S</Parameter>\n" +
						"<Parameter id=\"PALLET_NO\">" + palletno + "</Parameter>\n" +
						"</Parameters>\n" +
						"<Dataset id=\"__DS_TRANS_INFO__\">\n" +
						"<ColumnInfo>\n" +
						"<Column id=\"strSvcID\" type=\"string\" size=\"256\"  />\n" +
						"<Column id=\"strURL\" type=\"string\" size=\"256\"  />\n" +
						"<Column id=\"strInDatasets\" type=\"string\" size=\"256\"  />\n" +
						"<Column id=\"strOutDatasets\" type=\"string\" size=\"256\"  />\n" +
						"</ColumnInfo>\n" +
						"<Rows>\n" +
						"<Row>\n" +
						"<Col id=\"strSvcID\">search</Col>\n" +
						"<Col id=\"strURL\">/nexacroController.do</Col>\n" +
						"<Col id=\"strInDatasets\" />\n" +
						"<Col id=\"strOutDatasets\">output1</Col>\n" +
						"</Row>\n" +
						"</Rows>\n" +
						"</Dataset>\n" +
						"</Root>";
		return xml;
	}

	public static String RectInfo(String rectno){
		String xml =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
						"<Root xmlns=\"http://www.nexacroplatform.com/platform/dataset\">" +
						"<Parameters>" +
						"<Parameter id=\"method\">getList</Parameter>\n" +
						"<Parameter id=\"sqlId\">PPE020QR_PALLET_NO_S</Parameter>\n" +
						"<Parameter id=\"RECT_NO\">" + rectno + "</Parameter>\n" +
						"</Parameters>\n" +
						"<Dataset id=\"__DS_TRANS_INFO__\">\n" +
						"<ColumnInfo>\n" +
						"<Column id=\"strSvcID\" type=\"string\" size=\"256\"  />\n" +
						"<Column id=\"strURL\" type=\"string\" size=\"256\"  />\n" +
						"<Column id=\"strInDatasets\" type=\"string\" size=\"256\"  />\n" +
						"<Column id=\"strOutDatasets\" type=\"string\" size=\"256\"  />\n" +
						"</ColumnInfo>\n" +
						"<Rows>\n" +
						"<Row>\n" +
						"<Col id=\"strSvcID\">search</Col>\n" +
						"<Col id=\"strURL\">/nexacroController.do</Col>\n" +
						"<Col id=\"strInDatasets\" />\n" +
						"<Col id=\"strOutDatasets\">output1</Col>\n" +
						"</Row>\n" +
						"</Rows>\n" +
						"</Dataset>\n" +
						"</Root>";
		return xml;
	}

	public static String PickingShipmentInfo(String sino){
		String xml =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
						"<Root xmlns=\"http://www.nexacroplatform.com/platform/dataset\">\n" +
						"<Parameters>\n" +
						"<Parameter id=\"method\">getList</Parameter>\n" +
						"<Parameter id=\"sqlId\">WMF115CT_DS_DETAIL1_S</Parameter>\n" +
						"<Parameter id=\"SI_NO\">" + sino +"</Parameter>\n" +
						"<Parameter id=\"REG_PGM_ID\">WMF115CT</Parameter>\n" +
						"<Parameter id=\"DEF_LANG\">ko_KR</Parameter>\n" +
						"<Parameter id=\"COMPANY_CD\">001</Parameter>\n" +
						"</Parameters>\n" +
						"<Dataset id=\"__DS_TRANS_INFO__\">\n" +
						"<ColumnInfo>\n" +
						"<Column id=\"strSvcID\" type=\"string\" size=\"256\"  />\n" +
						"<Column id=\"strURL\" type=\"string\" size=\"256\"  />\n" +
						"<Column id=\"strInDatasets\" type=\"string\" size=\"256\"  />\n" +
						"<Column id=\"strOutDatasets\" type=\"string\" size=\"256\"  />\n" +
						"</ColumnInfo>\n" +
						"<Rows>\n" +
						"<Row>\n" +
						"<Col id=\"strSvcID\">ds_detail1search</Col>\n" +
						"<Col id=\"strURL\">/nexacroController.do</Col>\n" +
						"<Col id=\"strInDatasets\" />\n" +
						"<Col id=\"strOutDatasets\">output1</Col>\n" +
						"</Row>\n" +
						"</Rows>\n" +
						"</Dataset>\n" +
						"</Root>";
		return xml;
	}

	public static String ExcutePickingShipment(String sino, String palletno){
		String xml =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
						"<Root xmlns=\"http://www.nexacroplatform.com/platform/dataset\">\n" +
						"<Parameters>\n" +
						"<Parameter id=\"method\">wmf115CtDetail1Insert</Parameter>\n" +
						"<Parameter id=\"REG_PGM_ID\">WMF115CT</Parameter>\n" +
						"<Parameter id=\"DEF_LANG\">ko_KR</Parameter>\n" +
						"<Parameter id=\"COMPANY_CD\">001</Parameter>\n" +
						"</Parameters>\n" +
						"<Dataset id=\"__DS_TRANS_INFO__\">\n" +
						"<ColumnInfo>\n" +
						"<Column id=\"strSvcID\" type=\"string\" size=\"256\"  />\n" +
						"<Column id=\"strURL\" type=\"string\" size=\"256\"  />\n" +
						"<Column id=\"strInDatasets\" type=\"string\" size=\"256\"  />\n" +
						"<Column id=\"strOutDatasets\" type=\"string\" size=\"256\"  />\n" +
						"</ColumnInfo>\n" +
						"<Rows>\n" +
						"<Row>\n" +
						"<Col id=\"strSvcID\">wmf115CtDetail1Insert</Col>\n" +
						"<Col id=\"strURL\">/WM_Controller.do</Col>\n" +
						"<Col id=\"strInDatasets\">input1</Col>\n" +
						"</Row>\n" +
						"</Rows>\n" +
						"</Dataset>\n" +
						"<Dataset id=\"input1\">\n" +
						"<ColumnInfo>\n" +
						"<Column id=\"SI_NO\" type=\"STRING\" size=\"256\"  />\n" +
						"<Column id=\"PALLET_NO\" type=\"STRING\" size=\"256\"  />\n" +
						"</ColumnInfo>\n" +
						"<Rows>\n" +
						"<Row type=\"insert\">\n" +
						"<Col id=\"SI_NO\">" + sino + "</Col>\n" +
						"<Col id=\"PALLET_NO\">" + palletno + " </Col>\n" +
						"</Row>\n" +
						"</Rows>\n" +
						"</Dataset>\n" +
						"</Root>";
		return xml;
	}

	public static String StackLocationInfo(String rectNo){
		String whouseCd = rectNo.substring(0, 4);
		String locGroup = rectNo.substring(5, 7);
		String xml =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
						"<Root xmlns=\"http://www.nexacroplatform.com/platform/dataset\">" +
						"<Parameters>" +
						"<Parameter id=\"method\">getList</Parameter>\n" +
						"<Parameter id=\"sqlId\">POC130QR_WHOUSE_STACK_LOCATION_INFO_S</Parameter>\n" +
						"<Parameter id=\"COMPANY_CD\">001</Parameter>\n" +
						"<Parameter id=\"WHOUSE_CD\">" + whouseCd + "</Parameter>\n" +
						"<Parameter id=\"IS_FULL_RACK\">0</Parameter>\n" +
						"<Parameter id=\"LOCATION_GROUP\">" + locGroup + "</Parameter>\n" +
						"<Parameter id=\"LOCATION_CD\">" + rectNo + "</Parameter>\n" +
						"</Parameters>\n" +
						"<Dataset id=\"__DS_TRANS_INFO__\">\n" +
						"<ColumnInfo>\n" +
						"<Column id=\"strSvcID\" type=\"string\" size=\"256\"  />\n" +
						"<Column id=\"strURL\" type=\"string\" size=\"256\"  />\n" +
						"<Column id=\"strInDatasets\" type=\"string\" size=\"256\"  />\n" +
						"<Column id=\"strOutDatasets\" type=\"string\" size=\"256\"  />\n" +
						"</ColumnInfo>\n" +
						"<Rows>\n" +
						"<Row>\n" +
						"<Col id=\"strSvcID\">search</Col>\n" +
						"<Col id=\"strURL\">/nexacroController.do</Col>\n" +
						"<Col id=\"strInDatasets\" />\n" +
						"<Col id=\"strOutDatasets\">output1</Col>\n" +
						"</Row>\n" +
						"</Rows>\n" +
						"</Dataset>\n" +
						"</Root>";
		return xml;
	}

	public static String StackPalletToRack(String flag, String locationcd, String palletno){
		String xml =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
						"<Root xmlns=\"http://www.nexacroplatform.com/platform/dataset\">" +
						"<Parameters>" +
						"<Parameter id=\"method\">getList</Parameter>\n" +
						"<Parameter id=\"sqlId\">POC130QR_STACK_LOATION_PALLET_INOUT_IU</Parameter>\n" +
						"<Parameter id=\"COMPANY_CD\">001</Parameter>\n" +
						"<Parameter id=\"STACK_LOCATION_CD\">" + locationcd + "</Parameter>\n" +
						"<Parameter id=\"PALLET_NO\">" + palletno + "</Parameter>\n" +
						"<Parameter id=\"REG_PGM_ID\">STACK_PALLET_ANDROID</Parameter>\n" +
						"<Parameter id=\"REG_USER\">" + Application.USER.getMap().get("USER_NO") + "</Parameter>\n" +
						"<Parameter id=\"INOUT_FLAG\">" + flag + "</Parameter>\n" +
						"</Parameters>\n" +
						"<Dataset id=\"__DS_TRANS_INFO__\">\n" +
						"<ColumnInfo>\n" +
						"<Column id=\"strSvcID\" type=\"string\" size=\"256\"  />\n" +
						"<Column id=\"strURL\" type=\"string\" size=\"256\"  />\n" +
						"<Column id=\"strInDatasets\" type=\"string\" size=\"256\"  />\n" +
						"<Column id=\"strOutDatasets\" type=\"string\" size=\"256\"  />\n" +
						"</ColumnInfo>\n" +
						"<Rows>\n" +
						"<Row>\n" +
						"<Col id=\"strSvcID\">search</Col>\n" +
						"<Col id=\"strURL\">/nexacroController.do</Col>\n" +
						"<Col id=\"strInDatasets\" />\n" +
						"<Col id=\"strOutDatasets\">output1</Col>\n" +
						"</Row>\n" +
						"</Rows>\n" +
						"</Dataset>\n" +
						"</Root>";
		return xml;
	}

	public static String PalletMove(ArrayList<List<Row>> items, String whouseCd, int flag){
		String xml =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
						"<Root xmlns=\"http://www.nexacroplatform.com/platform/dataset\">" +
						"<Parameters>\n" +
						"<Parameter id=\"method\">save</Parameter>\n" +
						"<Parameter id=\"sqlId\">POC130QR_STOCK_MOVE_OUTIN</Parameter>\n" +
						"<Parameter id=\"forceSqlFlag\">N</Parameter>\n" +
						"<Parameter id=\"REG_PGM_ID\">ANDROID-MES</Parameter>\n" +
						"<Parameter id=\"REG_USER\">11111</Parameter>\n" +
						"<Parameter id=\"REG_IP\">11111</Parameter>\n" +
						"<Parameter id=\"COMPANY_CD\">001</Parameter>  \n" +
						"<Parameter id=\"STACK_LOCATION_CD\">" + (items.get(0).get(0).getMap().get("STACK_LOCATION_CD") == null ? "" : items.get(0).get(0).getMap().get("STACK_LOCATION_CD")) + "</Parameter>  \n" +
						"<Parameter id=\"TO_WHOUSE_CD\">" + whouseCd + "</Parameter>\n" +
						"<Parameter id=\"INOUT_FLAG\">" + (flag == 1 ? "OUT" : "IN") + "</Parameter>\n" +
						"<Parameter id=\"DEF_LANG\">ko_KR</Parameter>\n" +
						"</Parameters>" +
						"<Dataset id=\"__DS_TRANS_INFO__\">\n" +
						"<ColumnInfo>\n" +
						"<Column id=\"strSvcID\" type=\"string\" size=\"256\"  />\n" +
						"<Column id=\"strURL\" type=\"string\" size=\"256\"  />\n" +
						"<Column id=\"strInDatasets\" type=\"string\" size=\"256\"  />\n" +
						"<Column id=\"strOutDatasets\" type=\"string\" size=\"256\"  />\n" +
						"</ColumnInfo>\n" +
						"<Rows>\n" +
						"<Row>\n" +
						"<Col id=\"strSvcID\">search</Col>\n" +
						"<Col id=\"strURL\">/nexacroController.do</Col>\n" +
						"<Col id=\"strInDatasets\">input1</Col>\n" +
						"<Col id=\"strOutDatasets\" />" +
						"</Row>\n" +
						"</Rows>\n" +
						"</Dataset>\n" +
						"<Dataset id=\"input1\">\n" +
						"<ColumnInfo>\n" +
						"<Column id=\"BARCODE_TYPE\" type=\"STRING\" size=\"256\"  />\n" +
						"<Column id=\"BARCODE\" type=\"STRING\" size=\"256\"  />\n" +
						"</ColumnInfo>\n";

		for (List<Row> item : items) {
			xml += "<Rows>\n" +
					"<Row type=\"update\">\n" +
					"<Col id=\"BARCODE_TYPE\">" + item.get(0).getMap().get("TYPE") +"</Col>\n" +
					"<Col id=\"BARCODE\">" + (item.get(0).getMap().get("TYPE").equals("PALLET") ? item.get(0).getMap().get("PALLET_NO") : item.get(0).getMap().get("BOX_NO")) +"</Col>\n" +
					"</Row></Rows>\n";
		}

		xml += "</Dataset>" +
				"</Root>";
		return xml;
	}

	public static String InsertPalletToItems(List<Row> Items)
	{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<Root xmlns=\"http://www.nexacroplatform.com/platform/dataset\">\n" +
				"<Parameters>\n" +
					"<Parameter id=\"method\">save</Parameter>\n" +
					"<Parameter id=\"sqlId\">PPB999PALLET_UPDATE_NO</Parameter>\n" +
					"<Parameter id=\"forceSqlFlag\">N</Parameter>\n" +
					"<Parameter id=\"COMPANY_CD\">001</Parameter>\n" +
					"<Parameter id=\"PLANT_CD\">001</Parameter>\n" +
					"<Parameter id=\"REG_PGM_ID\">PPB999</Parameter>\n" +
					"<Parameter id=\"DEF_LANG\">ko_KR</Parameter>\n" +
				"</Parameters>\n" +
			"<Dataset id=\"__DS_TRANS_INFO__\">\n" +
				"<ColumnInfo>\n" +
					"<Column id=\"strSvcID\" type=\"string\" size=\"256\" />\n" +
					"<Column id=\"strURL\" type=\"string\" size=\"256\" />\n" +
					"<Column id=\"strInDatasets\" type=\"string\" size=\"256\" />\n" +
					"<Column id=\"strOutDatasets\" type=\"string\" size=\"256\" />\n" +
				"</ColumnInfo>\n" +
				"<Rows>\n" +
					"<Row>\n" +
						"<Col id=\"strSvcID\">save</Col>\n" +
						"<Col id=\"strURL\">/nexacroController.do</Col>\n" +
						"<Col id=\"strInDatasets\">input1</Col>\n" +
						"<Col id=\"strOutDatasets\" />\n" +
					"</Row>\n" +
				"</Rows>\n" +
			"</Dataset>\n" +
			"<Dataset id=\"input1\">\n" +
				"<ColumnInfo>\n" +
					"<Column id=\"BOX_NO\" type=\"STRING\" size=\"256\"  />\n" +
					"<Column id=\"PALLET_NO\" type=\"STRING\" size=\"256\"  />\n" +
				"</ColumnInfo>\n" +
				"<Rows>";

		for (Row item : Items) {
			xml += "<Row type=\"insert\">";
			xml += "<Col id=\"BOX_NO\">" + item.getMap().get("BOX_NO") + "</Col>";
			xml += "<Col id=\"PALLET_NO\">" + item.getMap().get("PALLET_NO") + "</Col>";
			xml += "</Row>";
		}

		xml +="</Rows>\n" +
			"</Dataset>\n" +
			"</Root>\n";
		return xml;
	}

	public static String ShipmentList(String sino){
		String xml =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
						"<Root xmlns=\"http://www.nexacroplatform.com/platform/dataset\">\n" +
						"<Parameters>\n" +
						"<Parameter id=\"method\">getList</Parameter>\n" +
						"<Parameter id=\"sqlId\">WMF115CT_DS_HEAD_S</Parameter>\n" +
						"<Parameter id=\"COMPANY_CD\">001</Parameter>\n" +
						"<Parameter id=\"PLANT_CD\">001</Parameter>\n" +
						"<Parameter id=\"OUTPUT_DT_FROM\">20000101</Parameter>\n" +
						"<Parameter id=\"OUTPUT_DT_TO\">99999999</Parameter>\n" +
						"<Parameter id=\"SI_FLAG\">S</Parameter>\n" +
						"<Parameter id=\"REG_PGM_ID\">WMF115CT</Parameter>\n" +
						"<Parameter id=\"SI_NO\">" + sino +"</Parameter>\n" +
						"<Parameter id=\"DEF_LANG\">ko_KR</Parameter>\n" +
						"<Parameter id=\"COMPANY_CD\">001</Parameter>\n" +
						"</Parameters>\n" +
						"<Dataset id=\"__DS_TRANS_INFO__\">\n" +
						"<ColumnInfo>\n" +
						"<Column id=\"strSvcID\" type=\"string\" size=\"256\" />\n" +
						"<Column id=\"strURL\" type=\"string\" size=\"256\" />\n" +
						"<Column id=\"strInDatasets\" type=\"string\" size=\"256\" />\n" +
						"<Column id=\"strOutDatasets\" type=\"string\" size=\"256\" />\n" +
						"</ColumnInfo>\n" +
						"<Rows>\n" +
						"<Row>\n" +
						"<Col id=\"strSvcID\">msearch</Col>\n" +
						"<Col id=\"strURL\">/nexacroController.do</Col>\n" +
						"<Col id=\"strInDatasets\" />\n" +
						"<Col id=\"strOutDatasets\">output1</Col>\n" +
						"</Row>\n" +
						"</Rows>\n" +
						"</Dataset>\n" +
						"</Root>";
		return xml;
	}

}

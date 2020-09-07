package com.isom.solaceproducer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

public class TransformShipment {
	
	private JSONObject sourceshipmentjson;
	
	public JSONObject transformShipmentToJson(String sourcexmlmessage){
		
		JSONObject isomshipmentjson= xmltojsonconverter(sourcexmlmessage);
		
		JSONObject shipmentlinenoextn=customMapper(isomshipmentjson);
		
		System.out.println("*****FINAL MESSAGE*****\n"+ shipmentlinenoextn.toString());
		
		return shipmentlinenoextn;
				
	}//End of transformShipmentToJson
	
	public JSONObject xmltojsonconverter(String xmlmessage) {
				
		try {
		//Converting XML Message to JSON Object
		sourceshipmentjson = XML.toJSONObject(xmlmessage);
		
		//Fetching element ShipmentLine from body
		JSONObject jsonshipmentline=sourceshipmentjson.getJSONObject("ISOMShipment").getJSONObject("Body").getJSONObject("Shipment").getJSONObject("ShipmentLines").getJSONObject("ShipmentLine");
		
		//Converting ShipmentLine to JSONArray
		JSONArray shipmentarray =new JSONArray().put(new JSONObject(jsonshipmentline.toString()));
        
		//Putting ShipmentArray in the main JSON
		sourceshipmentjson.getJSONObject("ISOMShipment").getJSONObject("Body").getJSONObject("Shipment").getJSONObject("ShipmentLines").put("ShipmentLine", shipmentarray);
		
		return sourceshipmentjson.getJSONObject("ISOMShipment").getJSONObject("Body");	
	}
		catch (Exception je) {
	
	  return sourceshipmentjson.getJSONObject("ISOMShipment").getJSONObject("Body");
  }
		
}//End of function xmltojsonconverter

	public JSONObject customMapper(JSONObject sourceshipmentlineextnjson) {
		
		try {
			
		//Adding values from Shipment Extn to Shipment
		sourceshipmentlineextnjson.getJSONObject("Shipment").accumulate("CduID", sourceshipmentlineextnjson.getJSONObject("Shipment").getJSONObject("Extn").get("CduID"));
		sourceshipmentlineextnjson.getJSONObject("Shipment").accumulate("WorkOrderNo", sourceshipmentlineextnjson.getJSONObject("Shipment").getJSONObject("Extn").get("WorkOrderNo"));
		sourceshipmentlineextnjson.getJSONObject("Shipment").accumulate("AssocDSLineNo", sourceshipmentlineextnjson.getJSONObject("Shipment").getJSONObject("Extn").get("AssocDSLineNo"));
		sourceshipmentlineextnjson.getJSONObject("Shipment").accumulate("OrderNo", sourceshipmentlineextnjson.getJSONObject("Shipment").getJSONObject("ShipmentLines").getJSONArray("ShipmentLine").getJSONObject(0).get("OrderNo"));
		
		//Removing Extn from Shipment
		sourceshipmentlineextnjson.getJSONObject("Shipment").remove("Extn");

		//Loop for fetching ShipmentLine Extn values
		for (int i = 0, size = sourceshipmentlineextnjson.getJSONObject("Shipment").getJSONObject("ShipmentLines").getJSONArray("ShipmentLine").length(); i < size; i++)
	    {			
	      JSONObject shipmentlineextn = sourceshipmentlineextnjson.getJSONObject("Shipment").getJSONObject("ShipmentLines").getJSONArray("ShipmentLine").getJSONObject(i).getJSONObject("Extn");
	      
	      //Adding ShipmentLine/Extn elements to ShipmentLine
	      sourceshipmentlineextnjson.getJSONObject("Shipment").getJSONObject("ShipmentLines").getJSONArray("ShipmentLine").getJSONObject(i).accumulate("OwnerOrganizationCode", shipmentlineextn.get("OwnerOrganizationCode"));
	      sourceshipmentlineextnjson.getJSONObject("Shipment").getJSONObject("ShipmentLines").getJSONArray("ShipmentLine").getJSONObject(i).accumulate("ItemSupplier", shipmentlineextn.get("ItemSupplier"));
	      sourceshipmentlineextnjson.getJSONObject("Shipment").getJSONObject("ShipmentLines").getJSONArray("ShipmentLine").getJSONObject(i).accumulate("DispatchedDate", shipmentlineextn.get("DispatchedDate"));
	      sourceshipmentlineextnjson.getJSONObject("Shipment").getJSONObject("ShipmentLines").getJSONArray("ShipmentLine").getJSONObject(i).accumulate("ConsignmentLineNo", shipmentlineextn.get("ConsignmentLineNo"));
	      sourceshipmentlineextnjson.getJSONObject("Shipment").getJSONObject("ShipmentLines").getJSONArray("ShipmentLine").getJSONObject(i).accumulate("ConsignmentNo", shipmentlineextn.get("ConsignmentNo"));
	      
		  //Removing Extn from ShipmentLine
	      sourceshipmentlineextnjson.getJSONObject("Shipment").getJSONObject("ShipmentLines").getJSONArray("ShipmentLine").getJSONObject(i).remove("Extn");
	      sourceshipmentlineextnjson.getJSONObject("Shipment").getJSONObject("ShipmentLines").getJSONArray("ShipmentLine").getJSONObject(i).remove("OrderNo");
			
	    }
		return sourceshipmentlineextnjson;
		}
		catch(JSONException e) {
			e.printStackTrace();
			throw e;
		}
		}
}//End of class
		
	
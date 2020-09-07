package com.isom.solaceproducer;

import org.json.JSONObject;

public class MessageMetadata {

	private String srcmessageid;
	private String srccompartment;
	private String srcevent;
	private String srcversion;
	private String solacetopicname;
	private JSONObject finalmessage;
	private String solacehost;
	private String solaceuser;
	private String solacepassword;
	
	public String getSrcmessageid() {
		return srcmessageid;
	}
	public void setSrcmessageid(String srcmessageid) {
		this.srcmessageid = srcmessageid;
	}
	public String getSrccompartment() {
		return srccompartment;
	}
	public void setSrccompartment(String srccompartment) {
		this.srccompartment = srccompartment;
	}
	public String getSrcevent() {
		return srcevent;
	}
	public void setSrcevent(String srcevent) {
		this.srcevent = srcevent;
	}
	public String getSrcversion() {
		return srcversion;
	}
	public void setSrcversion(String srcversion) {
		this.srcversion = srcversion;
	}
	public String getSolacetopicname() {
		return solacetopicname;
	}
	public void setSolacetopicname(String solacetopicname) {
		this.solacetopicname = solacetopicname;
	}
	public JSONObject getFinalmessage() {
		return finalmessage;
	}
	public void setFinalmessage(JSONObject finalmessage) {
		this.finalmessage = finalmessage;
	}
	public String getSolacehost() {
		return solacehost;
	}
	public void setSolacehost(String solacehost) {
		this.solacehost = solacehost;
	}
	public String getSolaceuser() {
		return solaceuser;
	}
	public void setSolaceuser(String solaceuser) {
		this.solaceuser = solaceuser;
	}
	public String getSolacepassword() {
		return solacepassword;
	}
	public void setSolacepassword(String solacepassword) {
		this.solacepassword = solacepassword;
	}

}

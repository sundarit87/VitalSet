package com.ideas2it.vitaltrendservice.model;

public class PayloadRequest {
	
	private Long executionTime;
	private String signature;
	private String object;
	
	public PayloadRequest() {
	}
	
	public PayloadRequest(Long executionTime, String signature, String object) {
		this.executionTime = executionTime;
		this.signature = signature;
		this.object = object;
	}

	public Long getExecutionTime() {
		return executionTime;
	}
	public void setExecutionTime(Long executionTime) {
		this.executionTime = executionTime;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}

	@Override
	public String toString() {
		return "SchedulerEventPayloadRequest [executionTime=" + executionTime + ", signature=" + signature + ", object="
				+ object + "]";
	}
	
}

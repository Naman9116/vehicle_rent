package com.util;

public class OperationMessage {

	public static String[] getOperationMessageType(String operationMessage) {
		String[] messageType = new String[2];
		if (operationMessage.equalsIgnoreCase("saveError")) {
			messageType[0] = "Entry Is Not Saved";
			messageType[1] = "error";
		}
		if (operationMessage.equalsIgnoreCase("updateError")) {
			messageType[0] = "Entry Is Not Updated";
			messageType[1] = "error";
		}
		if (operationMessage.equalsIgnoreCase("deleteError")) {
			messageType[0] = "Entry Is Not Deleted";
			messageType[1] = "error";
		} 
		else if (operationMessage.equalsIgnoreCase("saveSuccess")) {
			messageType[0] = "Entry Saved Successfully";
			messageType[1] = "success";
		} 
		else if (operationMessage.equalsIgnoreCase("updateSuccess")) {
			messageType[0] = "Entry Updated Successfully";
			messageType[1] = "success";
		}
		else if (operationMessage.equalsIgnoreCase("deleteSuccess")) {
			messageType[0] = "Entry Deleted Successfully";
			messageType[1] = "success";
		} 
		else if (operationMessage.equalsIgnoreCase("duplicateRecord")) {
			messageType[0] = "Duplicate Record";
			messageType[1] = "error";
		} 
		else if (operationMessage.equalsIgnoreCase("validationFailed")) {
			messageType[0] = "Validation Failed";
			messageType[1] = "error";
		} 
		else if (operationMessage.equalsIgnoreCase("uniqueKeyConstraint")) {
			messageType[0] = "Updation is Duplicate";
			messageType[1] = "error";
		} 
		else if (operationMessage.equalsIgnoreCase("foreignKeyConstraint")) {
			messageType[0] = "Wrong Deletion {Foreign Key Constraint}";
			messageType[1] = "error";
		}
		return messageType;
	}
	
	
}

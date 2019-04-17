package pt.unl.fct.di.apdc.evaluation.util;

public class RegisterData {
	public String username;
	public String password;
	public String confirmation;
	public String email;
	public String name;
	public String role;
	public String profileStatus;
	public String telephoneNumber;
	public String cellphoneNumber;
	public String address;
	
	public RegisterData(){
		
	}
	//Default
	public RegisterData(String username, String password, String confirmation, String email, String name, String telephoneNumber, String cellphoneNumber,String address){
		this.username = username;
		this.password = password;
		this.confirmation = confirmation;
		this.email = email;
		this.name = name;
		this.role = "USER";
		this.profileStatus = "public";
		this.telephoneNumber = telephoneNumber;
		this.cellphoneNumber = cellphoneNumber;
		this.address = address;
	}
	// BackOffice way to add any desired type of user
	public RegisterData(String username, String password, String confirmation, String email, String name, String role, String profileStatus, String telephoneNumber, String cellphoneNumber,String address){
		this.username = username;
		this.password = password;
		this.confirmation = confirmation;
		this.email = email;
		this.name = name;
		this.role = role;
		this.profileStatus = profileStatus;
		this.telephoneNumber = telephoneNumber;
		this.cellphoneNumber = cellphoneNumber;
		this.address = address;	
	}
	//Default with no address
	public RegisterData(String username, String password, String confirmation, String email, String name, String telephoneNumber, String cellphoneNumber){
		this.username = username;
		this.password = password;
		this.confirmation = confirmation;
		this.email = email;
		this.name = name;
		this.role = "USER";
		this.profileStatus = "public";
		this.telephoneNumber = telephoneNumber;
		this.cellphoneNumber = cellphoneNumber;
		this.address = null;
	}
	}

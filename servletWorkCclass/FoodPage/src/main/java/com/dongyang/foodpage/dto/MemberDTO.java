package com.dongyang.foodpage.dto;

public class MemberDTO {
    private String id;
    private String pw;
    private String name;   // DB 컬럼과 동일하게 name
    private String role;
    private String allergy;
    private String comp;
    private String home;
    

    public MemberDTO() {}

    public MemberDTO(String id, String pw, String name, String role,String allergy,String comp,String home) {
        this.id = id;
        this.pw = pw;
        this.name = name;
        this.role = role;
        this.allergy = allergy;
        this.comp = comp;
        this.home = home;
        
    }




	// Getter
    public String getId() { return id; }
    public String getPw() { return pw; }
    public String getName() { return name; }   // 반드시 getName()
    public String getRole() { return role; }
    public String getAllergy() { return allergy; }
	public String getComp() { return comp; }
	public String getHome() { return home; }


    // Setter
    public void setId(String id) { this.id = id; }
    public void setPw(String pw) { this.pw = pw; }
    public void setName(String name) { this.name = name; }   // 반드시 setName()
	public void setAllergy(String allergy) { this.allergy = allergy; }
    public void setRole(String role) { this.role = role; }
    public void setComp(String comp) { this.comp = comp; }
	public void setHome(String home) { this.home = home; }


}
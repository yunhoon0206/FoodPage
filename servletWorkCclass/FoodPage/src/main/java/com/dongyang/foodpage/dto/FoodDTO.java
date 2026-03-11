package com.dongyang.foodpage.dto;
import java.util.*;
import com.dongyang.foodpage.dto.ParsedIngredientDTO;

public class FoodDTO {
    private String name;//요리이름
    private String time;//조리시간 > 기존 정보엔없고 따로 로직을 통해 계산 
    private String img;// 이미지파일주소 
    private String ingredients; // 재료목록 전체 텍스트 
    private List<ParsedIngredientDTO> parsedIngredients; // 구조화된 재료 목록
    private List<String> manuals; // 조리 과정 (순서대로)
    private String rcpSeq; // 레시피 고유 번호
    private String category; // 요리 종류 (RCP_PAT2)

    public FoodDTO() {}

    public FoodDTO(String name, String time, String img) {
        this.name = name;
        this.time = time;
        this.img = img;
    }
    // rcpSeq를 포함하는 생성자 추가
    public FoodDTO(String name, String time, String img, String rcpSeq) {
        this.name = name;
        this.time = time;
        this.img = img;
        this.rcpSeq = rcpSeq;
    }


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public List<ParsedIngredientDTO> getParsedIngredients() { return parsedIngredients; }
    public void setParsedIngredients(List<ParsedIngredientDTO> parsedIngredients) { this.parsedIngredients = parsedIngredients; }

    public List<String> getManuals() { return manuals; }
    public void setManuals(List<String> manuals) { this.manuals = manuals; }

    public String getRcpSeq() { return rcpSeq; }
    public void setRcpSeq(String rcpSeq) { this.rcpSeq = rcpSeq; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
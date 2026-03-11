package com.dongyang.foodpage.dto;

public class PathDTO {
    private int trafficType;     // 1=지하철, 2=버스, 3=도보
    private int sectionTime;     // 해당 구간 시간
    private String name;         // 버스 번호, 지하철 노선, 도보일 경우 "도보"

    private String startName;    // 선택
    private String endName;      // 선택
    private String laneInfo;     // 선택: "5626" 또는 "2호선" 등

    public PathDTO() {}

    public PathDTO(int trafficType, int sectionTime, String name,
                   String startName, String endName, String laneInfo) {
        this.trafficType = trafficType;
        this.sectionTime = sectionTime;
        this.name = name;
        this.startName = startName;
        this.endName = endName;
        this.laneInfo = laneInfo;
    }

    public int getTrafficType() { return trafficType; }
    public void setTrafficType(int trafficType) { this.trafficType = trafficType; }

    public int getSectionTime() { return sectionTime; }
    public void setSectionTime(int sectionTime) { this.sectionTime = sectionTime; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStartName() { return startName; }
    public void setStartName(String startName) { this.startName = startName; }

    public String getEndName() { return endName; }
    public void setEndName(String endName) { this.endName = endName; }

    public String getLaneInfo() { return laneInfo; }
    public void setLaneInfo(String laneInfo) { this.laneInfo = laneInfo; }
}
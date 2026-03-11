package com.dongyang.foodpage.dto;

import java.util.List;

public class RouteResultDTO
{
    private int totalTime;
    private List<PathDTO> paths;

    public RouteResultDTO() {}
    public RouteResultDTO(int totalTime, List<PathDTO> paths) {
        this.totalTime = totalTime;
        this.paths = paths;
    }

    public int getTotalTime() { return totalTime; }
    public void setTotalTime(int totalTime) { this.totalTime = totalTime; }

    public List<PathDTO> getPaths() { return paths; }
    public void setPaths(List<PathDTO> paths) { this.paths = paths; }
}
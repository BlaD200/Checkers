package org.ukma.vsynytsyn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameData {
    private String status;

    @JsonProperty(value = "whose_turn")
    private String whoseTurn;

    private String winner;

    private List<Cell> board;

    @JsonProperty(value = "available_time")
    private double availableTime;

    @JsonProperty(value = "is_started")
    private boolean isStarted;

    @JsonProperty(value = "is_finished")
    private boolean isFinished;


    @Override
    public String toString() {
        return "GameData{" +
                "status='" + status + '\'' +
                ", whoseTurn='" + whoseTurn + '\'' +
                ", winner='" + winner + '\'' +
                ", board=" + "..." +
                ", availableTime=" + availableTime +
                ", isStarted=" + isStarted +
                ", isFinished=" + isFinished +
                '}';
    }
}

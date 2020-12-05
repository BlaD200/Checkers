package org.ukma.vsynytsyn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameStatus {

    private String status;
    private GameData data;
}


@Data
@NoArgsConstructor
@AllArgsConstructor
class Cell {
    private String color;
    private int row;
    private int column;
    private boolean king;
    private int position;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
class GameData {
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
}
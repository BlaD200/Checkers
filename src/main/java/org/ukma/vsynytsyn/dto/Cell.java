package org.ukma.vsynytsyn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cell {
    private PlayerColor color;
    private int row;
    private int column;
    private boolean king;
    private int position;

    public boolean isRed() {
        return color == PlayerColor.RED;
    }
}

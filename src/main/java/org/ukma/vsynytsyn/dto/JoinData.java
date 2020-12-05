package org.ukma.vsynytsyn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinData {
    private PlayerColor color;
    private String token;
}
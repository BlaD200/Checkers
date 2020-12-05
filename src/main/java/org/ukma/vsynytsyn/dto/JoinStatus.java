package org.ukma.vsynytsyn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinStatus {

    private String status;
    private JoinData data;

}


@Data
@NoArgsConstructor
@AllArgsConstructor
class JoinData {
    private String color;
    private String token;
}

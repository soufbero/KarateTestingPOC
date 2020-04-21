package com.souf.karate.domain.api;

import com.souf.karate.util.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiRequest {
    private String userName;
    private String password;

    @Override
    public String toString() {
        return Utils.printObjectAsXMLString(this);
    }
}

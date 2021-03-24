package com.bencik.countries.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Route implements Serializable {

    private List<String> route;

    public Route(List<String> route) {
        this.route = route;
    }
}

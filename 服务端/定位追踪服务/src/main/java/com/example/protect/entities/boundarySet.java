package com.example.protect.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import springfox.documentation.spring.web.json.Json;

/**
 * @author Rex
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class boundarySet {
    public int account_id;
    public int boundary_id;
    public String boundary_set_json;
}

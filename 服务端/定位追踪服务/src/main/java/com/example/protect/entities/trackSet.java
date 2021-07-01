package com.example.protect.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Rex
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class trackSet {
    public String track_set_json;
    public long start_time;
    public long end_time;
    public int account_id;
}

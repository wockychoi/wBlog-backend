package com.mapago.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Audit {
    @JsonIgnore
    private String creId;

    @JsonIgnore
    private String updId;

    @JsonIgnore
    private LocalDateTime creDt;

    @JsonIgnore
    private LocalDateTime updDt;
}
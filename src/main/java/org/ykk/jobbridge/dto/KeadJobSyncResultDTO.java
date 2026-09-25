package org.ykk.jobbridge.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeadJobSyncResultDTO {

    private int fetchedCount;
    private int savedCount;
    private int pageCount;
    private String message;

}

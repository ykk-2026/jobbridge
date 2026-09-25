package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.KeadJobSyncResultDTO;

public interface IKeadJobService {

    KeadJobSyncResultDTO syncJobs() throws Exception;

}

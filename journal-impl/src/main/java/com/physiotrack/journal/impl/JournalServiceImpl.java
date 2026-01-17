package com.physiotrack.journal.impl;

import com.physiotrack.journal.api.JournalService;

public class JournalServiceImpl implements JournalService {
    @Override
    public String ping() {
        return "journal module OK";
    }
}

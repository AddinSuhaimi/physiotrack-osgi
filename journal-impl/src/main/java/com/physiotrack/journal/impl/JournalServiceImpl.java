package com.physiotrack.journal.impl;

import com.physiotrack.journal.api.JournalService;
import org.osgi.service.component.annotations.Component;

@Component(service = JournalService.class)
public class JournalServiceImpl implements JournalService {

    @Override
    public String ping() {
        return "journal service OK";
    }
}

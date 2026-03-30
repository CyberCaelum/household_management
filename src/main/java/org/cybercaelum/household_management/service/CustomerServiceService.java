package org.cybercaelum.household_management.service;

import org.cybercaelum.household_management.pojo.dto.MessageCallbackDTO;
import org.cybercaelum.household_management.pojo.dto.OpenimUserCallbackDTO;
import org.cybercaelum.household_management.pojo.dto.OpenimCallbackDTO;

public interface CustomerServiceService {
    OpenimCallbackDTO afterOnline(OpenimUserCallbackDTO userCallbackDTO);

    OpenimCallbackDTO afterOffLine(OpenimUserCallbackDTO userCallbackDTO);

    OpenimCallbackDTO freshCsGroup(MessageCallbackDTO messageCallbackDTO);
}

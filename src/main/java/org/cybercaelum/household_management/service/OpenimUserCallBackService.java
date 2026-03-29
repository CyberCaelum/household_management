package org.cybercaelum.household_management.service;

import org.cybercaelum.household_management.pojo.dto.OpenimUserCallbackDTO;
import org.cybercaelum.household_management.pojo.vo.OpenimCallbackVO;

public interface OpenimUserCallBackService {
    OpenimCallbackVO afterOnline(OpenimUserCallbackDTO userCallbackDTO);
}

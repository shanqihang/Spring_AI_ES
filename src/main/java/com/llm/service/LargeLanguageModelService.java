package com.llm.service;

import com.llm.Dto.CheckedResponse;

public interface LargeLanguageModelService {
    CheckedResponse getCheckedResponse(String prompt);
    CheckedResponse checkedDetect(String text);
}

package edu.pk.qurduplex.identityService.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class CodeGenerator {
    public String generateCode(int length){
        RandomStringUtils generator = RandomStringUtils.insecure();
        return generator.next(length, 'A', 'Z' + 1, false, false);
    }
}

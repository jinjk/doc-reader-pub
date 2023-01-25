package com.utilsrv.doc;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Getter
@Component
public class DocConfig {
    private static String configFileName = "/doc_file.properties";

    @Value("${docSvr.rootDir}")
    private String rootDir;
}

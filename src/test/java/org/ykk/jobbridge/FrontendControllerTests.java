package org.ykk.jobbridge;

import org.junit.jupiter.api.Test;
import org.ykk.jobbridge.controller.FrontendController;

import static org.assertj.core.api.Assertions.assertThat;

class FrontendControllerTests {

    @Test
    void rootAlwaysForwardsToBuiltFrontend() {
        assertThat(new FrontendController().index()).isEqualTo("forward:/index.html");
    }
}

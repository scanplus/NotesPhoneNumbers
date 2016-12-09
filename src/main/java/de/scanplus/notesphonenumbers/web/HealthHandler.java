package de.scanplus.notesphonenumbers.web;

import de.scanplus.notesphonenumbers.service.DominoSalesClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthHandler {

    private static final Logger LOG = LogManager.getLogger(HealthHandler.class);

    @Autowired
    private DominoSalesClient dsc;

    @RequestMapping("/ready")
    public ResponseEntity<Boolean> ready() {
        if (dsc.isReady()) {
            return ResponseEntity.ok(true);
        }
        LOG.warn("Domino client not ready");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
    }

    @RequestMapping("/live")
    public ResponseEntity<Boolean> live() {
        if (dsc.isReady()) {
            return ResponseEntity.ok(true);
        }
        LOG.warn("Domino client not ready");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
    }
}

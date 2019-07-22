package de.scanplus.notesphonenumbers.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.scanplus.notesphonenumbers.service.DominoSalesClient;
import de.scanplus.notesphonenumbers.service.UpdateAddressService;

@RestController
public class ManagerHandler {
    private static final Logger LOG = LogManager.getLogger(ManagerHandler.class);

    @Autowired
    private DominoSalesClient dsc;
    
    @Autowired
    private UpdateAddressService uas;
    
    @RequestMapping("/runupdate")
    public ResponseEntity<Boolean> runUpdate() {
        if (dsc.isReady()) {
            uas.execute();
            return ResponseEntity.ok(true);
        }
        LOG.warn("Domino client not ready");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
    }
}

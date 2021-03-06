package de.scanplus.notesphonenumbers.service;

import de.scanplus.notesphonenumbers.data.AddressData;
import de.scanplus.notesphonenumbers.data.AddressLink;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class UpdateAddressService {

    private static final String ENV_LOAD_ENTRIES_PER_REQUEST = "LOAD_ENTRIES_PER_REQUEST";
    
    private static final Logger LOG = LogManager.getLogger(UpdateAddressService.class);

    @Autowired
    private DominoSalesClient dsc;
    
    private int loadEntriesPerRequest = 50;

    public UpdateAddressService() {
        final String loadEntriesPerRequest = System.getenv(ENV_LOAD_ENTRIES_PER_REQUEST);
        try {
            this.loadEntriesPerRequest = Integer.parseInt(loadEntriesPerRequest);
        } catch (Exception ex) {
            LOG.warn("Could not load loadEntriesPerRequest from ENV; using default = "
                    + this.loadEntriesPerRequest);
        }
    }
    
        
    @Scheduled(fixedDelay = 1000 * 60 * 60, initialDelay = 1000 * 60)
    public void execute() {
        if (!dsc.isReady()) {
            LOG.error("Could not initialise API client");
            return;
        }
        final List<AddressLink> list = dsc.loadLinkList(0, loadEntriesPerRequest);
        int count = 1;
        for (final AddressLink al : list) {
            AddressData ad = dsc.loadAddressData(al);
            if (ad != null) {
                LOG.info("Success (" + count + "): " + dsc.saveAddressData(ad, al));
                count++;
            }
        }
    }

}

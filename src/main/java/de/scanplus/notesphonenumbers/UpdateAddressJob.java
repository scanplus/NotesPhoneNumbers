package de.scanplus.notesphonenumbers;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class UpdateAddressJob implements org.quartz.Job {

    private static final Logger LOG = LogManager.getLogger(UpdateAddressJob.class);

    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        final DominoSalesClient dsc = new DominoSalesClient();
        if (!dsc.isReady()) {
            LOG.error("Could not initialise API client");
            return;
        }
        final List<AddressLink> list = dsc.loadLinkList(0, 6000);
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

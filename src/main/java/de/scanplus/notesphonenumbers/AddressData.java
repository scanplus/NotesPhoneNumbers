/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.scanplus.notesphonenumbers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import java.time.ZonedDateTime;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author t.genannt
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressData {

    private static final Logger LOG = LogManager.getLogger(AddressData.class);

    private final String spPhoneNumber;

    private final String spMobileNumber;

    private final String spPrivatePhoneNumber;

    private final String spPrivateMobileNumber;

    private final ZonedDateTime spLastPhoneUpdate;

    @JsonCreator
    public AddressData(@JsonProperty("PhoneNumber") String phoneNumber, @JsonProperty("MobileNumber") String mobileNumber,
            @JsonProperty("PrivatPhoneNumber") String privatePhoneNumber, @JsonProperty("PrivatMobileNumber") String privateMobileNumber) {
        // normalize numbers to E164 format
        this.spPhoneNumber = normalizeNumber(phoneNumber);
        this.spMobileNumber = normalizeNumber(mobileNumber);
        this.spPrivatePhoneNumber = normalizeNumber(privatePhoneNumber);
        this.spPrivateMobileNumber = normalizeNumber(privateMobileNumber);
        this.spLastPhoneUpdate = ZonedDateTime.now();
    }

    @JsonProperty("spPhoneNumber")
    public String getSPPhoneNumber() {
        return this.spPhoneNumber;
    }

    @JsonProperty("spMobileNumber")
    public String getSPMobileNumber() {
        return this.spMobileNumber;
    }

    @JsonProperty("spLastPhoneUpdate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssz", locale = "en-US")
    public ZonedDateTime getSPLastPhoneUpdate() {
        return this.spLastPhoneUpdate;
    }

    @JsonProperty("spPrivatePhoneNumber")
    public String getSpPrivatePhoneNumber() {
        return this.spPrivatePhoneNumber;
    }

    @JsonProperty("spPrivateMobileNumber")
    public String getSpPrivateMobileNumber() {
        return this.spPrivateMobileNumber;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(15, 17)
                .append(this.spPhoneNumber).append(this.spMobileNumber)
                .append(this.spPrivatePhoneNumber).append(this.spPrivateMobileNumber)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("spPhoneNumber", this.spPhoneNumber)
                .append("spMobileNumber", this.spMobileNumber)
                .append("spPrivatePhoneNumber", this.spPrivatePhoneNumber)
                .append("spPrivateMobileNumber", this.spPrivateMobileNumber)
                .append("spLastPhoneUpdate", this.spLastPhoneUpdate)
                .toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AddressData) {
            AddressData rhs = (AddressData) obj;
            AddressData lhs = this;
            return new EqualsBuilder()
                    .append(lhs.spPhoneNumber, rhs.spPhoneNumber)
                    .append(lhs.spMobileNumber, rhs.spMobileNumber)
                    .append(lhs.spPrivatePhoneNumber, rhs.spPrivatePhoneNumber)
                    .append(lhs.spPrivateMobileNumber, rhs.spPrivateMobileNumber)
                    .isEquals();
        }
        return false;
    }

    private String normalizeNumber(String number) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            if (StringUtils.isNotBlank(number)) {
                Phonenumber.PhoneNumber parsedPhoneNumber = phoneUtil.parse(number, "DE");
                return phoneUtil.format(parsedPhoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
            }
        } catch (NumberParseException e) {
            LOG.debug("Number (" + number + ") could not be parsed", e);
        }
        return "";
    }

}

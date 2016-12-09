/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.scanplus.notesphonenumbers.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
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

    private final List<String> spPhoneNumber;

    private final List<String> spMobileNumber;

    private final List<String> spPrivatePhoneNumber;

    private final List<String> spPrivateMobileNumber;

    private final ZonedDateTime spLastPhoneUpdate;

    @JsonCreator
    public AddressData(@JsonProperty("PhoneNumber") List<String> phoneNumber, @JsonProperty("MobileNumber") List<String> mobileNumber,
            @JsonProperty("PrivatPhoneNumber") List<String> privatePhoneNumber, @JsonProperty("PrivatMobileNumber") List<String> privateMobileNumber) {
        // normalize numbers to E164 format
        this.spPhoneNumber = normalizeNumber(phoneNumber);
        this.spMobileNumber = normalizeNumber(mobileNumber);
        this.spPrivatePhoneNumber = normalizeNumber(privatePhoneNumber);
        this.spPrivateMobileNumber = normalizeNumber(privateMobileNumber);
        this.spLastPhoneUpdate = ZonedDateTime.now();
    }

    @JsonProperty("spPhoneNumber")
    public List<String> getSPPhoneNumber() {
        return this.spPhoneNumber;
    }

    @JsonProperty("spMobileNumber")
    public List<String> getSPMobileNumber() {
        return this.spMobileNumber;
    }

    @JsonProperty("spLastPhoneUpdate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssz", locale = "en-US")
    public ZonedDateTime getSPLastPhoneUpdate() {
        return this.spLastPhoneUpdate;
    }

    @JsonProperty("spPrivatePhoneNumber")
    public List<String> getSpPrivatePhoneNumber() {
        return this.spPrivatePhoneNumber;
    }

    @JsonProperty("spPrivateMobileNumber")
    public List<String> getSpPrivateMobileNumber() {
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

    private List<String> normalizeNumber(List<String> numbers) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        List<String> normalizedNumbers = new ArrayList<>();
        if (numbers == null) {
            return normalizedNumbers;
        }
        for (String number : numbers) {
            try {
                if (StringUtils.isNotBlank(number)) {
                    Phonenumber.PhoneNumber parsedPhoneNumber = phoneUtil.parse(number, "DE");
                    normalizedNumbers.add(phoneUtil.format(parsedPhoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164));
                }
            } catch (NumberParseException e) {
                LOG.debug("Number (" + number + ") could not be parsed", e);
            }
        }
        return normalizedNumbers;
    }

}

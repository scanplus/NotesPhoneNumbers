/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.scanplus.notesphonenumbers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressLink {

    public AddressLink() {
    }

    @JsonProperty("@link")
    private Link link;

    private class Link {

        @JsonProperty("href")
        String href;

        @JsonProperty("rel")
        String rel;

        public Link() {
        }

    }

    public String getLink() {
        return this.link.href;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("rel", this.link.rel)
                .append("href", this.link.href).toString();
    }

}

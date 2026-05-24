package model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class ShippingInformation {

    @Column(name = "postal_code", length = 6)
    @Size(min = 6, max = 6)
    @Pattern(regexp = "^[0-9]*$")
    private String postalCode;

    private String address;

    public ShippingInformation(String postalCode, String address) {
        this.postalCode = postalCode;
        this.address = address;
    }
}

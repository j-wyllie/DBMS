package odms.commons.model.enums;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public enum CountriesEnum {
    NZ("New Zealand"),
    AF("Afghanistan"),
    AL("Albania"),
    DZ("Algeria"),
    AD("Andorra"),
    AO("Angola"),
    AG("Antigua and Barbuda"),
    AR("Argentina"),
    AM("Armenia"),
    AW("Aruba"),
    AU("Australia"),
    AT("Austria"),
    AZ("Azerbaijan"),
    BS("Bahamas"),
    BH("Bahrain"),
    BD("Bangladesh"),
    BB("Barbados"),
    BY("Belarus"),
    BE("Belgium"),
    BZ("Belize"),
    BJ("Benin"),
    BM("Bermuda"),
    BT("Bhutan"),
    BO("Bolivia"),
    BA("Bosnia and Herzegovina"),
    BW("Botswana"),
    BR("Brazil"),
    BN("Brunei"),
    BG("Bulgaria"),
    BF("Burkina Faso"),
    BI("Burundi"),
    KH("Cambodia"),
    CM("Cameroon"),
    CA("Canada"),
    CV("Cape Verde"),
    CF("Central African Republic"),
    TD("Chad"),
    CL("Chile"),
    CN("China"),
    CO("Colombia"),
    KM("Comoros"),
    CG("Congo"),
    CD("Congo, The Democratic Republic of the"),
    CK("Cook Islands"),
    CR("Costa Rica"),
    CI("Cote D'Ivoire"),
    HR("Croatia"),
    CU("Cuba"),
    CY("Cyprus"),
    CZ("Czech Republic"),
    DK("Denmark"),
    DJ("Djibouti"),
    DM("Dominica"),
    DO("Dominican Republic"),
    EC("Ecuador"),
    EG("Egypt"),
    SV("El Salvador"),
    GQ("Equatorial Guinea"),
    ER("Eritrea"),
    EE("Estonia"),
    ET("Ethiopia"),
    FJ("Fiji"),
    FI("Finland"),
    FR("France"),
    GA("Gabon"),
    GM("Gambia"),
    GE("Georgia"),
    DE("Germany"),
    GH("Ghana"),
    GR("Greece"),
    GD("Grenada"),
    GT("Guatemala"),
    GN("Guinea"),
    GW("Guinea-Bissau"),
    GY("Guyana"),
    HT("Haiti"),
    VA("Holy See (Vatican City State)"),
    HN("Honduras"),
    HU("Hungary"),
    IS("Iceland"),
    IN("India"),
    ID("Indonesia"),
    IR("Iran, Islamic Republic Of"),
    IQ("Iraq"),
    IE("Ireland"),
    IL("Israel"),
    IT("Italy"),
    JM("Jamaica"),
    JP("Japan"),
    JO("Jordan"),
    KZ("Kazakhstan"),
    KE("Kenya"),
    KI("Kiribati"),
    KP("Korea, Democratic People's Republic of"),
    KR("Korea, Republic of"),
    KW("Kuwait"),
    KG("Kyrgyzstan"),
    LA("Lao People'S Democratic Republic"),
    LV("Latvia"),
    LB("Lebanon"),
    LS("Lesotho"),
    LR("Liberia"),
    LY("Libya"),
    LI("Liechtenstein"),
    LT("Lithuania"),
    LU("Luxembourg"),
    MK("Macedonia, The Former Yugoslav Republic of"),
    MG("Madagascar"),
    MW("Malawi"),
    MY("Malaysia"),
    MV("Maldives"),
    ML("Mali"),
    MT("Malta"),
    MH("Marshall Islands"),
    MQ("Martinique"),
    MR("Mauritania"),
    MU("Mauritius"),
    MX("Mexico"),
    FM("Micronesia, Federated States of"),
    MD("Moldova, Republic of"),
    MC("Monaco"),
    MN("Mongolia"),
    ME("Montenegro"),
    MA("Morocco"),
    MZ("Mozambique"),
    MM("Myanmar"),
    NA("Namibia"),
    NR("Nauru"),
    NP("Nepal"),
    NL("Netherlands"),
    NI("Nicaragua"),
    NE("Niger"),
    NG("Nigeria"),
    NU("Niue"),
    NO("Norway"),
    OM("Oman"),
    PK("Pakistan"),
    PW("Palau"),
    PS("Palestinian Territory, Occupied"),
    PA("Panama"),
    PG("Papua New Guinea"),
    PY("Paraguay"),
    PE("Peru"),
    PH("Philippines"),
    PL("Poland"),
    PT("Portugal"),
    QA("Qatar"),
    RO("Romania"),
    RU("Russian Federation"),
    RW("RWANDA"),
    KN("Saint Kitts and Nevis"),
    LC("Saint Lucia"),
    VC("Saint Vincent and the Grenadines"),
    WS("Samoa"),
    SM("San Marino"),
    ST("Sao Tome and Principe"),
    SA("Saudi Arabia"),
    SN("Senegal"),
    CS("Serbia"),
    SC("Seychelles"),
    SL("Sierra Leone"),
    SG("Singapore"),
    SK("Slovakia"),
    SI("Slovenia"),
    SB("Solomon Islands"),
    SO("Somalia"),
    ZA("South Africa"),
    SS("South Sudan"),
    ES("Spain"),
    LK("Sri Lanka"),
    SD("Sudan"),
    SR("Suriname"),
    SZ("Swaziland"),
    SE("Sweden"),
    CH("Switzerland"),
    SY("Syrian Arab Republic"),
    TW("Taiwan, Province of China"),
    TJ("Tajikistan"),
    TZ("Tanzania, United Republic of"),
    TH("Thailand"),
    TL("Timor-Leste"),
    TG("Togo"),
    TO("Tonga"),
    TT("Trinidad and Tobago"),
    TN("Tunisia"),
    TR("Turkey"),
    TM("Turkmenistan"),
    TV("Tuvalu"),
    UG("Uganda"),
    UA("Ukraine"),
    AE("United Arab Emirates"),
    GB("United Kingdom"),
    US("United States"),
    UY("Uruguay"),
    UZ("Uzbekistan"),
    VU("Vanuatu"),
    VE("Venezuela"),
    VN("Vietnam"),
    YE("Yemen"),
    ZM("Zambia"),
    ZW("Zimbabwe");

    private String name;

    private BooleanProperty valid = new SimpleBooleanProperty();

    CountriesEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Generate an ArrayList of strings of country names.
     *
     * @return the array list of countries
     */
    public static List<String> toArrayList() {
        ArrayList<CountriesEnum> countries = new ArrayList<>(EnumSet.allOf(CountriesEnum.class));
        ArrayList<String> countryStrings = new ArrayList<>();

        for (CountriesEnum country : countries) {
            countryStrings.add(country.getName());
        }

        return countryStrings;
    }

    /**
     * Gets the value of the enum from the name.
     *
     * @param name Name to be converted to enum.
     * @return The converted name to the country enum.
     */
    public static CountriesEnum getEnumByString(String name) {
        for (CountriesEnum e : CountriesEnum.values()) {
            if (name.equalsIgnoreCase(e.name)) {
                return e;
            }
        }
        return null;
    }

    /**
     * Creates a list of the enum country values.
     *
     * @return list of country enum values.
     */
    public static List<String> getValuesAsStrings() {
        List<String> countries = new ArrayList<>();
        for (CountriesEnum num : CountriesEnum.values()) {
            countries.add(num.toString());
        }
        return countries;
    }

    /**
     * Gets the name of a country.
     *
     * @param string string of country to be converted.
     * @return country name.
     */
    public static String getValidNameFromString(String string) {
        if (CountriesEnum.toArrayList().contains(string)) {
            return string;
        } else if (getValuesAsStrings().contains(string)) {
            CountriesEnum country = CountriesEnum.getEnumByString(string);
            return country != null ? country.getName() : "New Zealand";
        } else {
            return "New Zealand";
        }
    }

    public void setValid(Boolean valid) {
        this.valid.set(valid);
    }

    public Boolean getValid() {
        return valid.get();
    }

    public Observable getValidProperty() {
        return valid;
    }
}

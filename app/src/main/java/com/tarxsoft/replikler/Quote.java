package com.tarxsoft.replikler;

public class Quote {

    private String quoteName;
    private String quoteText;
    private String quoteLink;

    public Quote(){

    }
    public Quote(String quoteName, String quoteText, String quoteLink){
        this.quoteName = quoteName;
        this.quoteText = quoteText;
        this.quoteLink = quoteLink;

    }

    public String getQuoteName() {
        return quoteName;
    }

    public void setQuoteName(String quoteName) {
        this.quoteName = quoteName;
    }

    public String getQuoteText() {
        return quoteText;
    }

    public void setQuoteText(String quoteText) {
        this.quoteText = quoteText;
    }

    public String getQuoteLink() {
        return quoteLink;
    }

    public void setQuoteLink(String quoteLink) {
        this.quoteLink = quoteLink;
    }
}

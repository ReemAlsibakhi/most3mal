package com.reemsib.mst3jl.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Advert0 implements Parcelable {

    private Integer id;
    private String title;
    private String description;
    private Integer view_number;
    private Integer  price;
    private String  price_type;
    private String adv_type;
    private Integer accept_negotiation;
    private Integer allow_reply;
    private Integer allow_offer_location;
    private Boolean in_favorite;
    private Integer rate;
    private ArrayList<Image> images;
    private MainCategory mainCategory;
    private User user;
    private ModelYear year;
    //   var company: Company,
    private City city;
    private  ArrayList<Reviews> reviews;
    private String created_at;

    public Advert0() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getView_number() {
        return view_number;
    }

    public void setView_number(Integer view_number) {
        this.view_number = view_number;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getPrice_type() {
        return price_type;
    }

    public void setPrice_type(String price_type) {
        this.price_type = price_type;
    }

    public String getAdv_type() {
        return adv_type;
    }

    public void setAdv_type(String adv_type) {
        this.adv_type = adv_type;
    }

    public Integer getAccept_negotiation() {
        return accept_negotiation;
    }

    public void setAccept_negotiation(Integer accept_negotiation) {
        this.accept_negotiation = accept_negotiation;
    }

    public Integer getAllow_reply() {
        return allow_reply;
    }

    public void setAllow_reply(Integer allow_reply) {
        this.allow_reply = allow_reply;
    }

    public Integer getAllow_offer_location() {
        return allow_offer_location;
    }

    public void setAllow_offer_location(Integer allow_offer_location) {
        this.allow_offer_location = allow_offer_location;
    }

    public Boolean getIn_favorite() {
        return in_favorite;
    }

    public void setIn_favorite(Boolean in_favorite) {
        this.in_favorite = in_favorite;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }

    public MainCategory getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(MainCategory mainCategory) {
        this.mainCategory = mainCategory;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ModelYear getYear() {
        return year;
    }

    public void setYear(ModelYear year) {
        this.year = year;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public ArrayList<Reviews> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Reviews> reviews) {
        this.reviews = reviews;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    protected Advert0(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        title = in.readString();
        description = in.readString();
        if (in.readByte() == 0) {
            view_number = null;
        } else {
            view_number = in.readInt();
        }
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = in.readInt();
        }
        price_type = in.readString();
        adv_type = in.readString();
        if (in.readByte() == 0) {
            accept_negotiation = null;
        } else {
            accept_negotiation = in.readInt();
        }
        if (in.readByte() == 0) {
            allow_reply = null;
        } else {
            allow_reply = in.readInt();
        }
        if (in.readByte() == 0) {
            allow_offer_location = null;
        } else {
            allow_offer_location = in.readInt();
        }
        byte tmpIn_favorite = in.readByte();
        in_favorite = tmpIn_favorite == 0 ? null : tmpIn_favorite == 1;
        if (in.readByte() == 0) {
            rate = null;
        } else {
            rate = in.readInt();
        }
        images = in.createTypedArrayList(Image.CREATOR);
        mainCategory = in.readParcelable(MainCategory.class.getClassLoader());
        user = in.readParcelable(User.class.getClassLoader());
        year = in.readParcelable(ModelYear.class.getClassLoader());
        city = in.readParcelable(City.class.getClassLoader());
        reviews = in.createTypedArrayList(Reviews.CREATOR);
        created_at = in.readString();
    }

    public static final Creator<Advert0> CREATOR = new Creator<Advert0>() {
        @Override
        public Advert0 createFromParcel(Parcel in) {
            return new Advert0(in);
        }

        @Override
        public Advert0[] newArray(int size) {
            return new Advert0[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(id);
        }
        parcel.writeString(title);
        parcel.writeString(description);
        if (view_number == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(view_number);
        }
        if (price == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(price);
        }
        parcel.writeString(price_type);
        parcel.writeString(adv_type);
        if (accept_negotiation == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(accept_negotiation);
        }
        if (allow_reply == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(allow_reply);
        }
        if (allow_offer_location == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(allow_offer_location);
        }
        parcel.writeByte((byte) (in_favorite == null ? 0 : in_favorite ? 1 : 2));
        if (rate == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(rate);
        }
        parcel.writeTypedList(images);
        parcel.writeParcelable(mainCategory, i);
        parcel.writeParcelable(user, i);
        parcel.writeParcelable(year, i);
        parcel.writeParcelable(city, i);
        parcel.writeTypedList(reviews);
        parcel.writeString(created_at);
    }
}

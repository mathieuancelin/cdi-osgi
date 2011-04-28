package com.sample.web.spain;

import com.sample.web.api.Hotel;
import com.sample.web.api.HotelProvider;
import java.util.ArrayList;
import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import org.osgi.cdi.api.extension.annotation.Publish;

@Publish
@ApplicationScoped
public class SpainHotelProvider implements HotelProvider {

    @Override
    public Collection<Hotel> hotels() {
        Collection<Hotel> hotels = new ArrayList<Hotel>();
        hotels.add(new Hotel("Los Hostel", "Madrid", "Spain"));
        return hotels;
    }
}

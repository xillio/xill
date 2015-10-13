package nl.xillio.xill.services.inject;

import org.testng.annotations.Test;

import java.util.function.Supplier;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Created by ernst on 13/10/15.
 */
public class FactoryTest {

    /**
     * Test that the factory returns the provided Supplier
     */
    @Test
    public void supplierConstructor() {
        Supplier<String> supplier = () -> {return "";};

        Factory factory = new Factory(supplier);

        assertEquals(factory.get(), supplier);
    }

    /**
     * Test that the factory wraps the provided class in a Supplier
     */
    @Test
    public void anonymousConstructor() {
        Factory factory = new Factory(String.class);

        assertEquals(factory.get().getClass(), String.class);
    }



}
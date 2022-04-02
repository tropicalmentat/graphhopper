package com.graphhopper.routing.util;

import com.graphhopper.util.PMap;

public class FlagEncoders {
    // todonow: better get rid of all these methods except for the versions taking properties. these are just copied
    // from the old flag encoder constructors currently
    public static FlagEncoder createFoot() {
        return createFoot(new PMap());
    }

    public static FlagEncoder createFoot(PMap properties) {
        return VehicleEncodedValues.foot(properties);
    }

    protected static FlagEncoder createFoot(int speedBits, double speedFactor, boolean speedTwoDirections) {
        return createFoot("foot", speedBits, speedFactor, speedTwoDirections);
    }

    protected static FlagEncoder createFoot(String name, int speedBits, double speedFactor, boolean speedTwoDirections) {
        return createFoot(new PMap()
                .putObject("name", name)
                .putObject("speed_bits", speedBits)
                .putObject("speed_factor", speedFactor)
                .putObject("speed_two_directions", speedTwoDirections));
    }

    public static FlagEncoder createHike() {
        return createHike(new PMap());
    }

    public static FlagEncoder createHike(PMap properties) {
        return VehicleEncodedValues.hike(properties);
    }

    protected static FlagEncoder createHike(int speedBits, double speedFactor, boolean speedTwoDirections) {
        return createHike("hike", speedBits, speedFactor, speedTwoDirections);
    }

    protected static FlagEncoder createHike(String name, int speedBits, double speedFactor, boolean speedTwoDirections) {
        return VehicleEncodedValues.hike(
                new PMap()
                        .putObject("name", name)
                        .putObject("speed_bits", speedBits)
                        .putObject("speed_factor", speedFactor)
                        .putObject("speed_two_directions", speedTwoDirections
                        ));
    }

    public static FlagEncoder createWheelchair() {
        return createWheelchair(new PMap());
    }

    public static FlagEncoder createWheelchair(PMap properties) {
        return VehicleEncodedValues.wheelchair(properties);
    }

    protected static FlagEncoder createWheelchair(int speedBits, double speedFactor) {
        return createWheelchair(
                new PMap()
                        .putObject("speed_bits", speedBits)
                        .putObject("speed_factor", speedFactor)

        );
    }

    public static FlagEncoder createCar() {
        return createCar(new PMap());
    }

    public static FlagEncoder createCar(int speedBits, double speedFactor, int maxTurnCosts) {
        return createCar("car", speedBits, speedFactor, maxTurnCosts);
    }

    public static FlagEncoder createCar(String name, int speedBits, double speedFactor, int maxTurnCosts) {
        return createCar(name, speedBits, speedFactor, maxTurnCosts, false);
    }

    public static FlagEncoder createCar(int speedBits, double speedFactor, int maxTurnCosts, boolean speedTwoDirections) {
        return createCar("car", speedBits, speedFactor, maxTurnCosts, speedTwoDirections);
    }

    public static FlagEncoder createCar(String name, int speedBits, double speedFactor, int maxTurnCosts, boolean speedTwoDirections) {
        return createCar(new PMap()
                .putObject("name", name)
                .putObject("speed_bits", speedBits)
                .putObject("speed_factor", speedFactor)
                .putObject("max_turn_costs", maxTurnCosts)
                .putObject("speed_two_directions", speedTwoDirections)
        );
    }

    public static FlagEncoder createCar(PMap properties) {
        return VehicleEncodedValues.car(properties);
    }

    public static FlagEncoder createMotorcycle() {
        return createMotorcycle(new PMap());
    }

    public static FlagEncoder createMotorcycle(PMap properties) {
        return VehicleEncodedValues.motorcycle(properties);
    }

    public static FlagEncoder createCar4wd(PMap properties) {
        return VehicleEncodedValues.car4wd(properties);
    }

    public static FlagEncoder createRacingBike() {
        return createRacingBike(new PMap());
    }

    public static FlagEncoder createRacingBike(PMap properties) {
        return VehicleEncodedValues.racingbike(properties);
    }

    protected static FlagEncoder createRacingBike(int speedBits, double speedFactor, int maxTurnCosts) {
        return createRacingBike(new PMap()
                .putObject("speed_bits", speedBits)
                .putObject("speed_factor", speedFactor)
                .putObject("max_turn_costs", maxTurnCosts));
    }

    public static FlagEncoder createBike() {
        return createBike(new PMap());
    }

    public static FlagEncoder createBike(String name) {
        return createBike(new PMap().putObject("name", name));
    }

    public static FlagEncoder createBike(PMap properties) {
        return VehicleEncodedValues.bike(properties);
    }

    public static FlagEncoder createBike(int speedBits, double speedFactor, int maxTurnCosts, boolean speedTwoDirections) {
        return createBike("bike", speedBits, speedFactor, maxTurnCosts, speedTwoDirections);
    }

    public static FlagEncoder createBike(String name, int speedBits, double speedFactor, int maxTurnCosts, boolean speedTwoDirections) {
        return VehicleEncodedValues.bike(new PMap()
                .putObject("name", name)
                .putObject("speed_bits", speedBits)
                .putObject("speed_factor", speedFactor)
                .putObject("max_turn_costs", maxTurnCosts)
                .putObject("speed_two_directions", speedTwoDirections)
        );
    }

    public static FlagEncoder createBike2() {
        return createBike2(new PMap());
    }

    public static FlagEncoder createBike2(PMap properties) {
        return VehicleEncodedValues.bike2(properties);
    }

    public static FlagEncoder createMountainBike() {
        return createMountainBike(new PMap());
    }

    public static FlagEncoder createMountainBike(PMap properties) {
        return VehicleEncodedValues.mountainbike(properties);
    }

    protected static FlagEncoder createMountainBike(int speedBits, double speedFactor, int maxTurnCosts) {
        return createMountainBike(
                new PMap()
                        .putObject("speed_bits", speedBits)
                        .putObject("speed_factor", speedFactor)
                        .putObject("max_turn_costs", maxTurnCosts)
        );
    }

    public static FlagEncoder createRoadsFlagEncoder() {
        return VehicleEncodedValues.roads();
    }
}

package com.bc.s3mpc.isin;

public class IsinAPI {

    enum Raster {
        GRID_1_KM,
        GRID_500_M,
        GRID_250_M
    }

    private static final double TO_RAD = Math.PI / 180.0;

    public IsinPoint toGlobalMap(Raster raster, double lon, double lat) {
        final ProjectionParam projectionParam = getProjectionParam(raster);

        final Tile tile = new Tile();
        tile.init(projectionParam);

        return tile.forwardGlobalMap(lon * TO_RAD, lat * TO_RAD);
    }

    public IsinPoint toTileImageCoordinates(Raster raster, double lon, double lat) {
        final ProjectionParam projectionParam = getProjectionParam(raster);

        final Tile tile = new Tile();
        tile.init(projectionParam);

        return tile.forwardTileImage(lon * TO_RAD, lat * TO_RAD);
    }

    private ProjectionParam getProjectionParam(Raster raster) {
        ProjectionType projectionType;
        if (raster == Raster.GRID_1_KM) {
            projectionType = ProjectionType.ISIN_K;
        } else if (raster == Raster.GRID_500_M) {
            projectionType = ProjectionType.ISIN_H;
        } else if (raster == Raster.GRID_250_M) {
            projectionType = ProjectionType.ISIN_Q;
        } else {
            throw new RuntimeException("Illegal projection type");
        }

        return ProjectionParamFactory.get(projectionType);
    }
}

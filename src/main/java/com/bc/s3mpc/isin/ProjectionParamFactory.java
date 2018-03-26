package com.bc.s3mpc.isin;

public class ProjectionParamFactory {

    public static ProjectionParam get(ProjectionType projectionType) {
        final ProjectionParam projectionParam = new ProjectionParam();
        switch (projectionType) {
            case ISIN_K:
                projectionParam.projection = ProjectionType.ISIN_K;
                setIsinValues(projectionParam);
                break;

            case ISIN_H:
                projectionParam.projection = ProjectionType.ISIN_H;
                setIsinValues(projectionParam);
                break;

            case ISIN_Q:
                projectionParam.projection = ProjectionType.ISIN_Q;
                setIsinValues(projectionParam);
                break;
        }

        return projectionParam;
    }

    private static void setIsinValues(ProjectionParam projectionParam) {
        projectionParam.ul_xul = -20015109.354;
        projectionParam.ul_yul = 10007554.677;
        projectionParam.pixel_size = 926.62543305;
        projectionParam.sphere_code = -1;   // @todo 2 tb/tb do we need this? 2018-03-21
        projectionParam.sphere = 6371007.181;
        projectionParam.nl_tile = 1200;
        projectionParam.ns_tile = 1200;
        projectionParam.ntile_line = 18;
        projectionParam.ntile_samp = 36;
        projectionParam.nl_grid = 18 * 1200;
        projectionParam.ns_grid = 36 * 1200;
        projectionParam.nl_global = new int[]{90, 540, 1080, 4320};
        projectionParam.ns_global = new int[]{180, 1080, 2160, 8640};
        projectionParam.nl_offset = new int[]{0, 0, 0, 0};
        projectionParam.ns_offset = new int[]{0, 0, 0, 0};
    }
}
package com.bc.s3mpc.isin;

public class Tile {

    private static final double MAP_EPS2 = 0.5e-3 * 55.0;

    private long nl;
    private long ns;
    private long nl_tile;
    private long ns_tile;
    private long nl_offset;
    private long ns_offset;
    private long nl_p;
    private long ns_p;
    private double siz_x;
    private double siz_y;
    private double ul_x;
    private double ul_y;
    private IsinDef isinu;

    public void setNl(long nl) {
        this.nl = nl;
    }

    public long getNl() {
        return nl;
    }

    public void setNs(long ns) {
        this.ns = ns;
    }

    public long getNs() {
        return ns;
    }

    public void setNl_tile(long nl_tile) {
        this.nl_tile = nl_tile;
    }

    public long getNl_tile() {
        return nl_tile;
    }

    public void setNs_tile(long ns_tile) {
        this.ns_tile = ns_tile;
    }

    public long getNs_tile() {
        return ns_tile;
    }

    public void setNl_offset(long nl_offset) {
        this.nl_offset = nl_offset;
    }

    public long getNl_offset() {
        return nl_offset;
    }

    public void setNs_offset(long ns_offset) {
        this.ns_offset = ns_offset;
    }

    public long getNs_offset() {
        return ns_offset;
    }

    public void setNl_p(long nl_p) {
        this.nl_p = nl_p;
    }

    public long getNl_p() {
        return nl_p;
    }

    public void setNs_p(long ns_p) {
        this.ns_p = ns_p;
    }

    public long getNs_p() {
        return ns_p;
    }

    public void setSiz_x(double siz_x) {
        this.siz_x = siz_x;
    }

    public double getSiz_x() {
        return siz_x;
    }

    public void setSiz_y(double siz_y) {
        this.siz_y = siz_y;
    }

    public double getSiz_y() {
        return siz_y;
    }

    public void setUl_x(double ul_x) {
        this.ul_x = ul_x;
    }

    public double getUl_x() {
        return ul_x;
    }

    public void setUl_y(double ul_y) {
        this.ul_y = ul_y;
    }

    public double getUl_y() {
        return ul_y;
    }

    public void init(ProjectionParam params) {
        final double[] projParam = new double[13];

        projParam[0] = params.sphere;

        long nrow_half;
        int pixel_size_ratio = 0;
        switch (params.projection) {
            case ISIN_K:    // 1km
                nrow_half = 180 * 60;
                isinu = getIsinDef(nrow_half, params.sphere);
                projParam[8] = nrow_half * 2.0;
                projParam[10] = 1.0;
                pixel_size_ratio = 1;
                break;

            case ISIN_H:    // 500m
                nrow_half = 180 * 60 * 2;
                isinu = getIsinDef(nrow_half, params.sphere);
                projParam[8] = nrow_half * 2.0;
                projParam[10] = 1.0;
                pixel_size_ratio = 2;
                break;

            case ISIN_Q:    // 250m
                nrow_half = 180 * 60 * 4;
                isinu = getIsinDef(nrow_half, params.sphere);
                projParam[8] = nrow_half * 2.0;
                projParam[10] = 1.0;
                pixel_size_ratio = 4;
                break;
        }

        // pixel_size_ration is always > 0
        nl = params.nl_grid * pixel_size_ratio;
        ns = params.ns_grid * pixel_size_ratio;

        nl_tile = params.nl_tile * pixel_size_ratio;
        ns_tile = params.ns_tile * pixel_size_ratio;

        nl_offset = 0;
        ns_offset = 0;

        nl_p = nl;  // @todo 2 tb/tb can't we ignore this? 2018-03-22
        ns_p = ns;

        siz_x = params.pixel_size / pixel_size_ratio;
        //noinspection SuspiciousNameCombination - we have equal area pixel
        siz_y = siz_x;
        ;

        ul_x = params.ul_xul + (0.5 * siz_x);
        ul_y = params.ul_yul - (0.5 * siz_y);

        PGS_GCT_Init.forward(projParam);
        //PGS_GCT_Init.reverse(projParam);
    }

    static IsinDef getIsinDef(long nrow_half, double sphere) {
        final double half_pi = Math.PI / 2.0;
        final IsinDef isinDef = new IsinDef();

        isinDef.nrow_half = nrow_half;  // @todo 2 tb/tb can we move these to int?? 2ß18-03-21
        isinDef.nrow = 2 * nrow_half;    // @todo 2 tb/tb do we need this really??? 2018-03-21
        isinDef.sphere_inv = 1.0 / sphere;
        isinDef.ang_size_inv = isinDef.nrow / Math.PI;
        isinDef.icol_cen = new long[(int) nrow_half];
        isinDef.ncol_inv = new double[(int) isinDef.nrow];

        long ncol_cen = 0;
        for (int irow = 0; irow < nrow_half; irow++) {
            final double clat = half_pi * (1.0 - ((double) irow + 0.5) / nrow_half);
            long ncol = (long) ((2.0 * Math.cos(clat) * isinDef.nrow) + 0.5);
            if (ncol < 1) {
                ncol = 1;
            }

            isinDef.icol_cen[irow] = (ncol + 1) / 2;
            isinDef.ncol_inv[irow] = 1.0 / ((double) ncol);
            ncol_cen = ncol;    // @todo 3 tb/tb implement more clever than assigning in loop 2018-03-21
        }
        isinDef.col_dist_inv = ncol_cen / (2.0 * Math.PI * sphere);

        return isinDef;
    }

    public void invMap(double x, double y) {
        final double line_global = ((ul_y - y) / siz_y) - nl_offset;
        final double samp_global = ((x - ul_x) / siz_x) - ns_offset;

        System.out.println("samp_global = " + samp_global);
        System.out.println("line_global = " + line_global);
    }

    public IsinPoint invPix(double line_global, double samp_global) {
        if (line_global < (-0.5 - MAP_EPS2)  || line_global > (nl - 0.5 + MAP_EPS2)) {
            throw new RuntimeException("Map line out of range: " + line_global);
        }

        if (samp_global < (-0.5 - MAP_EPS2)  || samp_global > (ns - 0.5 + MAP_EPS2)) {
            throw new RuntimeException("Map row out of range: " + samp_global);
        }

        long iline_global = (long)(line_global + 0.5);
        if (iline_global >= nl_p) {
            iline_global = nl - 1;
        }
        if (iline_global < 0) {
            iline_global = 0;
        }

        long isamp_global = (long)(samp_global + 0.5);
        if (isamp_global >= ns_p) {
            isamp_global = ns - 1;
        }
        if (isamp_global < 0) {
            isamp_global = 0;
        }

        final int itile_line = (int) (iline_global / nl_tile);
        final int itile_samp = (int)(isamp_global / ns_tile);

        final double line = line_global - (itile_line * nl_tile);
        final double samp = samp_global - (itile_samp * ns_tile);

        return new IsinPoint(samp, line, itile_line, itile_samp);
    }
}

#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform vec4 u_outlineColor;
uniform float u_thickness;    // in texture coordinate units (like 1.0 / textureSize)
uniform float u_alphaThreshold;
uniform vec2 u_textureSize;

varying vec2 v_texCoords;

vec4 safeTexture2D(sampler2D tex, vec2 uv) {
    if (uv.x < 0.0 || uv.x > 1.0 || uv.y < 0.0 || uv.y > 1.0) {
        return vec4(0.0, 0.0, 0.0, 0.0); // transparent
    }
    return texture2D(tex, uv);
}

void main() {
    float threshold = u_alphaThreshold;
    float thickness = u_thickness;
    vec2 pixelSize = 1.0 / u_textureSize;

    vec4 current = safeTexture2D(u_texture, v_texCoords);
    if (current.a > threshold) {
        // Normal pixel
        discard;
    } else {
        // Sample surrounding 8 pixels
        float alphaSum = 0.0;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0) continue;
                vec2 offset = vec2(float(x), float(y)) * thickness * pixelSize;
                vec4 sample = safeTexture2D(u_texture, v_texCoords + offset);
                if(sample.a > threshold){
                    alphaSum += sample.a;
                }
            }
        }

        if (alphaSum > 0.0) {
            gl_FragColor = u_outlineColor;
        } else {
            discard;
        }
    }
}

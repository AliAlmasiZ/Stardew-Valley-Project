#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;

uniform int u_count;
uniform vec4 u_colors[10];
uniform float u_positions[10];
uniform float u_scrHeight[10];
varying vec2 v_texCoords;

void main() {
    if(texture2D(u_texture, v_texCoords).a > 3.0) discard;
    float y = clamp(1.0 - v_texCoords.y, 0.0, 1.0);
    vec4 c = u_colors[0];

    for (int i = 0; i < u_count; i++) {
        if (y >= u_positions[i] && y <= u_positions[i+1]) {
            float t = (y - u_positions[i]) / (u_positions[i+1] - u_positions[i]);
            c = mix(u_colors[i], u_colors[i+1], t);
        }
    }

    // If y is exactly at the top, use the last stop
    if (y >= u_positions[u_count - 1]) {
        c = u_colors[u_count - 1];
    }
    gl_FragColor = c;
}

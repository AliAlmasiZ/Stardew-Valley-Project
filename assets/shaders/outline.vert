attribute vec4 a_position;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;
uniform vec3 u_center;
uniform float u_scale;

varying vec2 v_texCoords;
void main() {
    v_texCoords = ((a_texCoord0 - vec2(0.5, 0.5)) * u_scale) + vec2(0.5, 0.5);

    vec3 posFromCenter = a_position.xyz - u_center;
    vec3 scaledPos = posFromCenter * u_scale;
    vec3 finalPos = scaledPos + u_center;

    gl_Position = u_projTrans * vec4(finalPos, 1.0);
}

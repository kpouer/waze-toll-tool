function check() {
    let tolls = toJsonObject().tolls;

    tolls.forEach(toll => {
        toll.sections.forEach(section => {
            section
                .segments
                .forEach(segment => fixSegment(segment));
        })
    });
}


function fixSegment(segment) {

}
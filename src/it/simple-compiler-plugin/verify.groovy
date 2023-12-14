

String buildLog = new File('target/it/simple-compiler-plugin/usage/target/pack/build.log').text


def matcher = buildLog =~ /found assignment/

assert matcher.size() == 5


matcher = buildLog =~ /on PARSE/
assert matcher.size() == 1
matcher = buildLog =~ /on ENTER/
assert matcher.size() == 3
matcher = buildLog =~ /on ANALYZE/
assert matcher.size() == 1
matcher = buildLog =~ /on GENERATE/
assert matcher.size() == 1
matcher = buildLog =~ /on ANNOTATION_PROCESSING[^_]/
assert matcher.size() == 1
matcher = buildLog =~ /on ANNOTATION_PROCESSING_ROUND/
assert matcher.size() == 2
matcher = buildLog =~ /on COMPILATION/
assert matcher.size() == 1
matcher = buildLog =~ /after PARSE/
assert matcher.size() == 1
matcher = buildLog =~ /after ENTER/
assert matcher.size() == 3
matcher = buildLog =~ /after ANALYZE/
assert matcher.size() == 1
matcher = buildLog =~ /after GENERATE/
assert matcher.size() == 1
matcher = buildLog =~ /after ANNOTATION_PROCESSING[^_]/
assert matcher.size() == 1
matcher = buildLog =~ /after ANNOTATION_PROCESSING_ROUND/
assert matcher.size() == 2
matcher = buildLog =~ /after COMPILATION/
assert matcher.size() == 1

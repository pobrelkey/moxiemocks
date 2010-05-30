#!/usr/bin/env ruby

#
# Script that generates several cubic yards of unit test code to provide 100% coverage of MoxieMatchers.
#

require 'stringio'

NUMERICS = %w(byte char short int long float double)
PRIMITIVE_MAP = {
	'boolean' => 'Boolean',
	'byte'    => 'Byte',
	'char'    => 'Character',
	'short'   => 'Short',
	'int'     => 'Integer',
	'long'    => 'Long',
	'float'   => 'Float',
	'double'  => 'Double'
}
TEST_NAMES = {}

def test(matcher_name, param_type, happy, expect_value, actual_value, wrap=false, array=false)
	expect_value = [expect_value] if !expect_value.is_a?(Array)
	actual_value = [actual_value] if !actual_value.is_a?(Array)
	expect_value = expect_value.collect{|x| x.to_s }
	actual_value = actual_value.collect{|x| x.to_s }

	expect_value.collect!{|x| munge_value(param_type, x) }
	actual_value.collect!{|x| munge_value(param_type, x) }

	result = StringIO.new
	if array
		upcased_type = param_type.sub(/^(\w)/){$1.upcase}
		generate_test(result, matcher_name, param_type, "array#{upcased_type}Call", happy, expect_value, actual_value, wrap)
		generate_test(result, matcher_name, param_type, "varargs#{upcased_type}Call", happy, expect_value, actual_value, wrap)
		if PRIMITIVE_MAP.has_key?(param_type)
			expect_value.collect! {|s| s.sub(Regexp.compile(Regexp.escape("new #{param_type}[]{")),"new #{PRIMITIVE_MAP[param_type]}[]{") }
			generate_test(result, matcher_name, param_type, "array#{upcased_type}ObjectCall", happy, expect_value, actual_value, wrap, PRIMITIVE_MAP[param_type] || param_type)
			generate_test(result, matcher_name, param_type, "varargs#{upcased_type}ObjectCall", happy, expect_value, actual_value, wrap, PRIMITIVE_MAP[param_type] || param_type)
		end
	else
		generate_test(result, matcher_name, param_type, "#{param_type.downcase}Call", happy, expect_value, actual_value, wrap) unless (PRIMITIVE_MAP.has_key?(param_type) && actual_value.detect{|x| x == 'null'})
		generate_test(result, matcher_name, param_type, "#{param_type.downcase}ObjectCall", happy, expect_value, actual_value, wrap) if PRIMITIVE_MAP.has_key?(param_type)
	end
	result.string
end

def munge_value(param_type, value)
	param_type = param_type.downcase
	prefix = suffix = nil
	value = "<#{value}>" if value !~ /\(/
	value.gsub!(/([\(<])([^\)>]*?)([\)>])/) {
		prefix, x, suffix = $1, $2, $3
		if param_type == 'string'
			x = ((x =~ /^".*"$/ || x == 'null') ? x : "\"#{x}\"")
		elsif param_type == 'object'
			x = (x =~ /^\d+$/ ? "Collections.singletonList(\"#{x}\")" : x)
		elsif param_type == 'char'
			x = (x =~ /^.$/ ? "'#{x}'" : x)
		elsif NUMERICS.include?(param_type) && param_type != 'int'
			x = (x != 'null' ? "(#{param_type}) #{x}" : x)
		end
		"#{prefix}#{x}#{suffix}"
	}
	value.gsub(/<([^>]*)>/){$1}
end

def generate_test(result, matcher_name, param_type, call_name, happy, expect_value, actual_value, wrap=false, upcased_type=param_type)
	test_name = "#{matcher_name.gsub(/<.*?>/,'')}_#{call_name}_#{happy ? 'happy' : 'sad'}Path"
	TEST_NAMES[test_name] = test_name_counter = ((TEST_NAMES[test_name] || 0) + 1)
	test_name << test_name_counter.to_s
	
	expect_values = expect_value.join(', ')
	expect_values = "new #{upcased_type}[]{#{expect_values}}" if wrap
	actual_values = actual_value.join(', ')
	actual_values = "new #{upcased_type}[]{#{actual_values}}" if wrap && call_name =~ /^array/

	result << (happy ? "\t@Test\n" : "\t@Test(expected=MoxieError.class)\n")
	result << "\tpublic void #{test_name}() {\n"
	result << "\t\tMoxie.expect(mock).will().#{call_name}(Moxie.#{matcher_name}(#{expect_values}));\n"
	result << "\t\tmock.#{call_name}(#{actual_values});\n"
	result << "\t}\n\n";
end


[NUMERICS, %w(String Object)].flatten.each do |type|
	print test('eq',  type, false, 2, 1)
	print test('eq',  type, true,  2, 2)
	print test('eq',  type, false, 2, 3)
	print test('eq',  type, false, 2, 'null')

	print test('not', type, true,  2, 1)
	print test('not', type, false, 2, 2)
	print test('not', type, true,  2, 3)
	print test('not', type, true,  2, 'null')

	any = 'any' + type.sub(/^(\w)/) { $1.upcase }
	print test(any, type, true, [], 1)
	print test(any, type, true, [], 2)
	print test(any, type, true, [], 3)
	print test(any, type, true, [], 'null')
end

print test('anything', 'object', true, [], 1)
print test('anything', 'object', true, [], 2)
print test('anything', 'object', true, [], 3)
print test('anything', 'object', true, [], 'null')

print test('eq',          'boolean', true,  true,  true)
print test('eq',          'boolean', false, true,  false)
print test('eq',          'boolean', false, true,  'null')
print test('eq',          'boolean', false, false, true)
print test('eq',          'boolean', true,  false, false)
print test('eq',          'boolean', false, false, 'null')
print test('not',         'boolean', false, true,  true)
print test('not',         'boolean', true,  true,  false)
print test('not',         'boolean', true,  true,  'null')
print test('not',         'boolean', true,  false, true)
print test('not',         'boolean', false, false, false)
print test('not',         'boolean', true,  false, 'null')
print test('anyBoolean',  'boolean', true,  [], true)
print test('anyBoolean',  'boolean', true,  [], false)
print test('anyBoolean',  'boolean', true,  [], 'null')
print test('booleanThat', 'boolean', true,  'new IsEqual(Boolean.TRUE)', true)
print test('booleanThat', 'boolean', false, 'new IsEqual(Boolean.TRUE)', false)
#print test('and',         'boolean', false, 'Moxie.eq(true), Moxie.eq(false)', true)
#print test('or',          'boolean', true,  'Moxie.eq(true), Moxie.eq(false)', true)
#print test('or',          'boolean', false, 'Moxie.eq(true), Moxie.eq(false)', 'null')
print test('aryEq', 'boolean', true,  [true,false,true], [true,false,true],       true, true)
print test('aryEq', 'boolean', false, [true,false,true], [true,false],            true, true)
print test('aryEq', 'boolean', false, [true,false,true], [true,false,true,false], true, true)
print test('aryEq', 'boolean', false, [true,false,true], [true,false,false],      true, true)
print test('array', 'boolean', true,  "Moxie.eq(true), Moxie.eq(false), Moxie.eq(true)", [true,false,true],       true, true)
print test('array', 'boolean', false, "Moxie.eq(true), Moxie.eq(false), Moxie.eq(true)", [true,false],            true, true)
print test('array', 'boolean', false, "Moxie.eq(true), Moxie.eq(false), Moxie.eq(true)", [true,false,true,false], true, true)
print test('array', 'boolean', false, "Moxie.eq(true), Moxie.eq(false), Moxie.eq(true)", [true,false,false],      true, true)



[NUMERICS, 'String'].flatten.each do |type|
	print test('leq', type, true,  2, 1)
	print test('leq', type, true,  2, 2)
	print test('leq', type, false, 2, 3)
	print test('leq', type, false, 2, 'null')
	
	print test('lt',  type, true,  2, 1)
	print test('lt',  type, false, 2, 2)
	print test('lt',  type, false, 2, 3)
	print test('lt',  type, false, 2, 'null')
	
	print test('geq', type, false, 2, 1)
	print test('geq', type, true,  2, 2)
	print test('geq', type, true,  2, 3)
	print test('geq', type, false, 2, 'null')
	
	print test('gt',  type, false, 2, 1)
	print test('gt',  type, false, 2, 2)
	print test('gt',  type, true,  2, 3)
	print test('gt',  type, false, 2, 'null')

	print test('and', type, false, "Moxie.lt(4), Moxie.gt(2)", 1, true)
	print test('and', type, true,  "Moxie.lt(4), Moxie.gt(2)", 3, true)
	print test('and', type, false, "Moxie.lt(4), Moxie.gt(2)", 5, true)
	print test('or',  type, true,  "Moxie.gt(4), Moxie.lt(2)", 1, true)
	print test('or',  type, false, "Moxie.gt(4), Moxie.lt(2)", 3, true)
	print test('or',  type, true,  "Moxie.gt(4), Moxie.lt(2)", 5, true)
	print test('not', type, true,  'Moxie.lt(3)', 5)
	print test('not', type, false, 'Moxie.lt(3)', 1)

	that = (type == 'String') ? '<String>argThat' : "#{type}That"
	print test(that, type, true,  'new IsEqual(2)', 2)
	print test(that, type, false, 'new IsEqual(2)', 1)

	print test('aryEq', type, true,  [1,2,3], [1,2,3],   true, true)
	print test('aryEq', type, false, [1,2,3], [1,2],     true, true)
	print test('aryEq', type, false, [1,2,3], [1,2,3,4], true, true)
	print test('aryEq', type, false, [1,2,3], [1,2,4],   true, true)

	print test('array', type, true,  "Moxie.lt(2), Moxie.lt(3), Moxie.lt(4)", [1,2,3],   true, true)
	print test('array', type, false, "Moxie.lt(2), Moxie.lt(3), Moxie.lt(4)", [1,2],     true, true)
	print test('array', type, false, "Moxie.lt(2), Moxie.lt(3), Moxie.lt(4)", [1,2,3,4], true, true)
	print test('array', type, false, "Moxie.lt(2), Moxie.lt(3), Moxie.lt(4)", [1,2,4],   true, true)
	print test('array', type, true,  "Moxie.lt(2), Moxie.lt(3), Moxie.lt(4)", [0,1,2],   true, true)
end

print test('isNull', 'object', true,  [], 'null')
print test('isNull', 'object', false, [], 'true')
%w(notNull isNotNull).each do |m|
	print test(m, 'object', false, [], 'null')
	print test(m, 'object', true,  [], 'true')
end

print test('hasSubstring', 'string', true,  'thor', 'Scunthorpe')
print test('hasSubstring', 'string', false, 'thor', 'Wodin')
print test('endsWith',     'string', true,  'ton',  'weighs a ton')
print test('endsWith',     'string', false, 'ton',  'weighs a thousand kilos')
print test('startsWith',   'string', true,  'Fred', 'Fred Flintstone')
print test('startsWith',   'string', false, 'Fred', 'Andrew Flintoff')

print test('anyArray', 'object', true,  [], 'null')
print test('anyArray', 'object', true,  [], 'new Object[]{"foo","bar"}')
print test('anyArray', 'object', true,  [], 'new int[]{1,2,3}')
print test('anyArray', 'object', false, [], '"blah"')

print test('any', 'object', true,  'String.class', 'null')
print test('any', 'object', true,  'String.class', '"blah"')
print test('any', 'object', false, 'String.class', 1)

print test('isA', 'object', false, 'String.class', 'null')
print test('isA', 'object', true,  'String.class', '"blah"')
print test('isA', 'object', false, 'String.class', 1)

%w(float double).each do |type|
	print test('eq', type, false, %w(4.0 2.0), 1)
	print test('eq', type, true,  %w(4.0 2.0), 3)
	print test('eq', type, true,  %w(4.0 2.0), 4)
	print test('eq', type, true,  %w(4.0 2.0), 5)
	print test('eq', type, false, %w(4.0 2.0), 7)
end

print test('aryEq', 'String', false, '"1"',           'null', true, true)
print test('array', 'String', false, 'Moxie.eq("1")', 'null', true, true)




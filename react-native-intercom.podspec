require 'json'
version = JSON.parse(File.read('package.json'))["version"]

Pod::Spec.new do |s|
  s.name             = 'RNIntercom'
  s.version          = version
  s.summary          = 'react-native-intercom'

  s.description      = <<-DESC
TODO: Add long description of the pod here.
                       DESC

  s.homepage         = 'https://github.com/shimo-react-native/react-native-intercom'
  s.license          = { :type => 'MIT', :file => 'LICENSE' }
  s.author           = { 'lisong' => 'lisong@shimo.im' }
  s.source           = { :git => 'https://github.com/shimo-react-native/react-native-intercom.git', :tag => s.version.to_s }

  s.ios.deployment_target = '8.0'
  
  s.source_files = 'iOS/**/*.{h,m,mm}'
  
  s.dependency 'React'
  s.dependency 'Intercom', '~> 4.1.0'

end

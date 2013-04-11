 <?php
/*
Template Name: Library landing
*/
?>
<?php get_header(); ?>
		
		<div role="main" class="main">
			<div class="page_title">
				<div class="wrapper">
					<hgroup>
						<h1>Library</h1>
						<h2>Everything goes in here!</h2>
					</hgroup>
				</div>
			</div><!--end page title-->
			
			<div class="main_content cf">
				<?php include_once "breadcrumbs.php"; ?>
				
				<div class="wrapper cf container">
					<div class="one_third tree_wrap">
						<nav class="tree_nav">
							<ul>
								<li>
									<a href="" class="expand closed">expand</a>
									<a href="">Architecture and Design</a>
									<ul>
										<li><a href="" class="expand closed"></a><a href="http://flodesign.co.uk">this is a sub-link</a></li>
										<li>
											<a href="" class="expand closed"></a>
											<a href="">this is a sub-link</a>
											<ul>
												<li><a href="http://flodesign.co.uk" class="doc">this is a sub-link</a></li>
												<li><a href="" class="doc">this is a document</a></li>
												<li><a href="" class="doc">this is a second document</a></li>
												<li><a href="" class="doc">this is yet anither document</a></li>
											</ul>
										</li>
										<li><a href="" class="expand"></a><a href="">this is a sub-link</a></li>
										<li><a href="" class="expand"></a><a href="">this is a sub-link</a></li>
									</ul>
								</li>
								<li>
									<a href="" class="expand closed">expand</a>
									<a href="">Technologies, Tools and Methods</a>
									<ul>
										<li><a href="" class="expand"></a><a href="http://flodesign.co.uk">this is a sub-link</a></li>
										<li><a href="" class="expand"></a><a href="">this is a sub-link</a></li>
										<li><a href="" class="expand"></a><a href="">this is a sub-link</a></li>
										<li><a href="" class="expand"></a><a href="">this is a sub-link</a></li>
									</ul>
								</li>
								<li><a href="" class="expand closed">expand</a>
									<a href="">Safe, Legal and Fair</a>
									<ul>
										<li><a href="" class="expand"></a><a href="http://flodesign.co.uk">this is a sub-link</a></li>
										<li><a href="" class="expand"></a><a href="">this is a sub-link</a></li>
										<li><a href="" class="expand"></a><a href="">this is a sub-link</a></li>
										<li><a href="" class="expand"></a><a href="">this is a sub-link</a></li>
									</ul>
								</li>
								<li><a href="" class="expand">expand</a>
									<a href="">Identifiers and Names</a>
								</li>
								<li>
									<a href="" class="expand">expand</a>
									<a href="">Interoperability and Integration</a>
								</li>
								<li>
									<a href="" class="expand">expand</a>
									<a href="">Transports and Networks</a>
								</li>
								<li>
									<a href="" class="expand">expand</a>
									<a href="">Service in SPINE</a>
								</li>
							</ul>
						</nav><!--end tree nav-->
					</div>
					
					<div class="two_thirds">
						<div class="content_wrap">
							
							<?php query_posts( array( 'post_type' => 'library',  'posts_per_page' => -1, 'orderby' => 'title' ) ); ?>
							<?php if ( have_posts() ) : while ( have_posts() ) : the_post(); ?>
							
							<div class="half">
								<a href="<?php the_permalink(); ?>" class="box">
									<div class="line green_line"></div>
									<section class="nav_box green">
										<h2><?php the_title(); ?></h2>
										<p><?php the_excerpt(); ?></p>
									</section>
								</a>
							</div>
							
							<?php endwhile; endif; wp_reset_query(); ?>
							
						</div><!--end content wrap-->	
					</div>
				</div><!--end wrapper-->
			</div><!--end main_content-->
		</div><!-- end main -->
		
<?php get_footer(); ?>		